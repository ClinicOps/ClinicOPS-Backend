package com.clinicops.domain.user.service;

import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinicops.common.exception.ValidationException;
import com.clinicops.common.audit.AuditPublisher;
import com.clinicops.common.audit.AuditRecord;
import com.clinicops.domain.access.model.AuthResponse;
import com.clinicops.domain.access.model.LoginRequest;
import com.clinicops.domain.access.model.RefreshToken;
import com.clinicops.domain.access.model.RegisterRequest;
import com.clinicops.domain.access.model.UserDTO;
import com.clinicops.domain.access.model.UserRoleAssignment;
import com.clinicops.domain.access.repository.RefreshTokenRepository;
import com.clinicops.domain.access.repository.RoleRepository;
import com.clinicops.domain.access.repository.UserRoleAssignmentRepository;
import com.clinicops.domain.clinic.model.Clinic;
import com.clinicops.domain.clinic.model.ClinicMember;
import com.clinicops.domain.clinic.model.ClinicStatus;
import com.clinicops.domain.clinic.model.MembershipStatus;
import com.clinicops.domain.clinic.repository.ClinicMemberRepository;
import com.clinicops.domain.clinic.repository.ClinicRepository;
import com.clinicops.domain.organization.model.Organization;
import com.clinicops.domain.organization.model.OrganizationStatus;
import com.clinicops.domain.organization.repository.OrganizationRepository;
import com.clinicops.domain.user.model.User;
import com.clinicops.domain.user.repository.UserRepository;
import com.clinicops.security.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ClinicMemberRepository clinicMemberRepository;
    private final OrganizationRepository organizationRepository;
    private final ClinicRepository clinicRepository;
    private final RoleRepository roleRepository;
    private final UserRoleAssignmentRepository userRoleAssignmentRepository;
    private final AuditPublisher auditPublisher;

    /**
     * Enhanced register flow:
     * 1. Validate input
     * 2. Create/find Organization
     * 3. Create Clinic under Organization
     * 4. Create User
     * 5. Assign OWNER role to user in clinic (UserRoleAssignment)
     * 6. Create ClinicMember entry
     * 7. Generate tokens
     * 8. Return AuthResponse with user details
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validation
        validateRegisterRequest(request);
        
        if (userRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new ValidationException("Email already registered");
        }

        // Step 1: Get or create Organization
        String orgName = request.getOrganizationName() != null && !request.getOrganizationName().isBlank()
                ? request.getOrganizationName()
                : "Default Org";
        
        Organization organization = new Organization();
        organization.setName(orgName);
        organization.setCode(generateOrgCode(orgName));
        organization.setStatus(OrganizationStatus.ACTIVE);
        organization.setDeleted(false);
        organization = organizationRepository.save(organization);
        log.info("Created organization: {} with ID: {}", organization.getName(), organization.getId());

        // Step 2: Create Clinic
        if (clinicRepository.existsByCode(request.getClinicCode())) {
            throw new ValidationException("Clinic code already exists");
        }
        
        Clinic clinic = new Clinic();
        clinic.setOrganizationId(organization.getId());
        clinic.setName(request.getClinicName());
        clinic.setCode(request.getClinicCode());
        clinic.setTimezone(request.getClinicTimezone() != null ? request.getClinicTimezone() : "UTC");
        clinic.setStatus(ClinicStatus.ACTIVE);
        clinic.setDeleted(false);
        clinic = clinicRepository.save(clinic);
        log.info("Created clinic: {} with ID: {}", clinic.getName(), clinic.getId());

        // Step 3: Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setDeleted(false);
        user = userRepository.save(user);
        log.info("Created user: {} with ID: {}", user.getEmail(), user.getId());

        // Step 4: Assign OWNER role via UserRoleAssignment
        var ownerRole = roleRepository.findByName("OWNER")
                .orElseThrow(() -> new ValidationException("OWNER role not found. Run data seeding."));
        
        UserRoleAssignment roleAssignment = new UserRoleAssignment();
        roleAssignment.setUserId(user.getId());
        roleAssignment.setClinicId(clinic.getId());
        roleAssignment.setRoleId(ownerRole.getId());
        roleAssignment.setStatus("ACTIVE");
        userRoleAssignmentRepository.save(roleAssignment);
        log.info("Assigned OWNER role to user {} in clinic {}", user.getId(), clinic.getId());

        // Step 5: Create ClinicMember entry (for backward compatibility with login)
        ClinicMember member = new ClinicMember();
        member.setUserId(user.getId());
        member.setOrganizationId(organization.getId());
        member.setClinicId(clinic.getId());
        member.setRole(com.clinicops.domain.clinic.model.ClinicRole.CLINIC_ADMIN);
        member.setStatus(MembershipStatus.ACTIVE);
        member.setDeleted(false);
        clinicMemberRepository.save(member);
        log.info("Created clinic membership for user {} in clinic {}", user.getId(), clinic.getId());

        // Step 6: Generate tokens
        String accessToken = jwtService.generateAccessToken(
                user.getId(), 
                organization.getId(), 
                clinic.getId(), 
                "OWNER"
        );

        String refreshToken = jwtService.generateRefreshToken(user.getId());
        
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUserId(user.getId());
        refreshTokenEntity.setTokenHash(
                org.apache.commons.codec.digest.DigestUtils.sha256Hex(refreshToken));
        refreshTokenEntity.setExpiryDate(java.time.Instant.now().plusSeconds(604800));
        refreshTokenEntity.setRevoked(false);
        refreshTokenRepository.save(refreshTokenEntity);
        log.info("Generated tokens for user {}", user.getId());

        // Step 7: Build UserDTO and return AuthResponse
        UserDTO userDTO = new UserDTO(
                user.getId().toString(),
                user.getEmail(),
                organization.getId().toString(),
                clinic.getId().toString(),
                clinic.getName(),
                clinic.getTimezone(),
                "OWNER"
        );

        // Step 8: Publish audit event for registration
        publishRegistrationAudit(user.getId(), clinic.getId(), organization.getId(), request.getEmail());

        return new AuthResponse(accessToken, refreshToken, userDTO);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new ValidationException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ValidationException("Invalid credentials");
        }

        var memberships =
                clinicMemberRepository.findByUserIdAndDeletedFalse(user.getId());

        ObjectId clinicId = null;
        ObjectId orgId = null;
        String role = null;
        String clinicName = null;
        String clinicTimezone = null;

        if (!memberships.isEmpty()) {
            var m = memberships.get(0);
            clinicId = m.getClinicId();
            orgId = m.getOrganizationId();
            role = m.getRole().name();
            
            // Fetch clinic details
            var clinic = clinicRepository.findById(clinicId);
            if (clinic.isPresent()) {
                clinicName = clinic.get().getName();
                clinicTimezone = clinic.get().getTimezone();
            }
        }

        String accessToken =
                jwtService.generateAccessToken(user.getId(), orgId, clinicId, role);

        String refreshToken =
                jwtService.generateRefreshToken(user.getId());

        RefreshToken entity = new RefreshToken();
        entity.setUserId(user.getId());
        entity.setTokenHash(
                org.apache.commons.codec.digest.DigestUtils.sha256Hex(refreshToken));
        entity.setExpiryDate(
                java.time.Instant.now().plusSeconds(604800));
        entity.setRevoked(false);

        refreshTokenRepository.save(entity);

        // Build UserDTO for response
        UserDTO userDTO = new UserDTO(
                user.getId().toString(),
                user.getEmail(),
                orgId != null ? orgId.toString() : null,
                clinicId != null ? clinicId.toString() : null,
                clinicName,
                clinicTimezone,
                role
        );

        return new AuthResponse(accessToken, refreshToken, userDTO);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ValidationException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("Password is required");
        }
        if (request.getPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
        if (request.getClinicName() == null || request.getClinicName().isBlank()) {
            throw new ValidationException("Clinic name is required");
        }
        if (request.getClinicCode() == null || request.getClinicCode().isBlank()) {
            throw new ValidationException("Clinic code is required");
        }
        // Email validation
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid email format");
        }
    }

    private String generateOrgCode(String orgName) {
        // Simple slug generation: replace spaces with hyphens, lowercase
        String code = orgName.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9-]", "");
        // Ensure uniqueness
        int counter = 1;
        String originalCode = code;
        while (organizationRepository.existsByCode(code)) {
            code = originalCode + "-" + counter++;
        }
        return code;
    }

    private void publishRegistrationAudit(ObjectId userId, ObjectId clinicId, ObjectId organizationId, String email) {
        try {
            var metadata = new java.util.HashMap<String, Object>();
            metadata.put("email", email);
            metadata.put("organizationId", organizationId.toString());
            metadata.put("clinicId", clinicId.toString());

            AuditRecord auditRecord = AuditRecord.builder()
                    .userId(userId)
                    .clinicId(clinicId.toString())
                    .domain("access")
                    .resource("user")
                    .action("register")
                    .timestamp(java.time.Instant.now())
                    .metadata(metadata)
                    .build();

            auditPublisher.publish(auditRecord);
            log.info("Published audit event for user registration: {}", email);
        } catch (Exception e) {
            log.error("Error publishing registration audit event", e);
            // Don't fail the request if audit event fails
        }
    }
}