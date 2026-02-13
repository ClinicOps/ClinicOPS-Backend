package com.clinicops.domain.access.service;

import com.clinicops.domain.access.model.Role;
import com.clinicops.domain.access.model.UserRoleAssignment;
import com.clinicops.domain.access.repository.PermissionRepository;
import com.clinicops.domain.access.repository.RoleRepository;
import com.clinicops.domain.access.repository.UserRoleAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PermissionEvaluator {

    private final RoleRepository roleRepo;
    private final UserRoleAssignmentRepository assignmentRepo;
    private final PermissionRepository permissionRepo;

    public PermissionEvaluator(
            RoleRepository roleRepo,
            UserRoleAssignmentRepository assignmentRepo,
            PermissionRepository permissionRepo) {
        this.roleRepo = roleRepo;
        this.assignmentRepo = assignmentRepo;
        this.permissionRepo = permissionRepo;
    }

    public boolean isAllowed(
            String userId,
            String clinicId,
            String domain,
            String resource,
            String action) {

        var assignments =
                assignmentRepo.findByUserIdAndClinicIdAndStatus(
                        userId, clinicId, "ACTIVE");

        Set<String> permissionKeys = new HashSet<>();

        for (UserRoleAssignment a : assignments) {
            Role role = roleRepo.findById(a.getRoleId()).orElse(null);
            if (role == null) continue;

            if (role.isOwner()) return true;

            permissionRepo.findAllById(role.getPermissionIds())
                    .forEach(p -> permissionKeys.add(p.key()));
        }

        return permissionKeys.contains(domain + ":" + resource + ":" + action);
    }
}
