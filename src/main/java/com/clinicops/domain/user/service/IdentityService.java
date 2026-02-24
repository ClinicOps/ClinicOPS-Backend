package com.clinicops.domain.user.service;

import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.clinicops.domain.access.model.AuthResponse;
import com.clinicops.domain.access.model.LoginRequest;
import com.clinicops.domain.access.model.RefreshToken;
import com.clinicops.domain.access.model.RegisterRequest;
import com.clinicops.domain.access.repository.RefreshTokenRepository;
import com.clinicops.domain.clinic.repository.ClinicMemberRepository;
import com.clinicops.domain.user.model.User;
import com.clinicops.domain.user.repository.UserRepository;
import com.clinicops.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ClinicMemberRepository clinicMemberRepository;

    public void register(RegisterRequest request) {

        if (userRepository.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setDeleted(false);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        var memberships =
                clinicMemberRepository.findByUserIdAndDeletedFalse(user.getId());

        ObjectId clinicId = null;
        ObjectId orgId = null;
        String role = null;

        if (!memberships.isEmpty()) {
            var m = memberships.get(0);
            clinicId = m.getClinicId();
            orgId = m.getOrganizationId();
            role = m.getRole().name();
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

        return new AuthResponse(accessToken, refreshToken);
    }
}