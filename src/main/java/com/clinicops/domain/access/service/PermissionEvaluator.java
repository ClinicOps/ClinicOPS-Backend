package com.clinicops.domain.access.service;

import com.clinicops.domain.access.model.Role;
import com.clinicops.domain.access.model.UserRoleAssignment;
import com.clinicops.domain.access.repository.PermissionRepository;
import com.clinicops.domain.access.repository.RoleRepository;
import com.clinicops.domain.access.repository.UserRoleAssignmentRepository;
import com.clinicops.infra.redis.PermissionCacheService;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionEvaluator {

    private final RoleRepository roleRepo;
    private final UserRoleAssignmentRepository assignmentRepo;
    private final PermissionRepository permissionRepo;
    private final PermissionCacheService cacheService;

    /**
     * Check if user has permission to perform action on resource in domain
     * Uses Redis caching to avoid repeated DB queries
     */
    public boolean isAllowed(
            ObjectId userObjId,
            String clinicId,
            String domain,
            String resource,
            String action) {
        
        ObjectId clinicObjId = new ObjectId(clinicId);
        String permissionKey = domain + ":" + resource + ":" + action;

        // Step 1: Try to get cached permissions
        Set<String> cachedPermissions = cacheService.getPermissions(
            userObjId.toString(), 
            clinicId
        );

        if (cachedPermissions != null) {
            log.debug("Using cached permissions for user: {} clinic: {}", userObjId, clinicId);
            return cachedPermissions.contains(permissionKey);
        }

        // Step 2: Fetch from database
        Set<String> permissionKeys = new HashSet<>();
        var assignments = assignmentRepo.findByUserIdAndClinicIdAndStatus(
            userObjId, clinicObjId, "ACTIVE"
        );

        for (UserRoleAssignment a : assignments) {
            Role role = roleRepo.findById(a.getRoleId()).orElse(null);
            if (role == null) continue;

            // OWNER role bypasses all checks
            if (role.isOwner()) {
                log.debug("User {} is OWNER, granting all permissions", userObjId);
                // Cache all permissions as empty set won't match, so store a marker
                cacheService.cachePermissions(userObjId.toString(), clinicId, null);
                return true;
            }

            // Add all permissions from this role
            permissionRepo.findAllById(role.getPermissionIds())
                .forEach(p -> permissionKeys.add(p.key()));
        }

        // Step 3: Cache the permissions
        cacheService.cachePermissions(userObjId.toString(), clinicId, permissionKeys);
        
        boolean allowed = permissionKeys.contains(permissionKey);
        log.debug("Permission check - User: {} Domain: {} Resource: {} Action: {} Result: {}", 
            userObjId, domain, resource, action, allowed);
        
        return allowed;
    }
}
