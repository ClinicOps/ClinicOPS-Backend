package com.clinicops.bootstrap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.clinicops.domain.access.model.Permission;
import com.clinicops.domain.access.model.Role;
import com.clinicops.domain.access.repository.PermissionRepository;
import com.clinicops.domain.access.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleAndPermissionSeeder {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void seedRolesAndPermissions() {
        try {
            log.info("Starting Role and Permission seeding...");

            // First, create all permissions
            Map<String, Permission> permissionsMap = createAllPermissions();

            // Then, create roles with their permissions
            createRoles(permissionsMap);

            log.info("Role and Permission seeding completed successfully!");
        } catch (Exception e) {
            log.error("Error during Role and Permission seeding", e);
        }
    }

    private Map<String, Permission> createAllPermissions() {
        Map<String, Permission> permissionsMap = new java.util.HashMap<>();
        
        // Check if permissions already exist and load them
        long count = permissionRepository.count();
        if (count > 0) {
            log.info("Permissions already exist in database (count: {}), loading from DB", count);
            // Load existing permissions into map
            permissionRepository.findAll().forEach(perm -> {
                permissionsMap.put(perm.key(), perm);
            });
            return permissionsMap;
        }

        // ========== CLINIC PERMISSIONS ==========
        Permission[] clinicPerms = {
                new Permission("clinic", "clinic", "create"),
                new Permission("clinic", "clinic", "view"),
                new Permission("clinic", "clinic", "update"),
                new Permission("clinic", "clinic", "delete"),
        };
        for (Permission perm : clinicPerms) {
            Permission saved = permissionRepository.save(perm);
            permissionsMap.put(perm.key(), saved);
        }

        // ========== USER PERMISSIONS ==========
        Permission[] userPerms = {
                new Permission("user", "user", "create"),
                new Permission("user", "user", "view"),
                new Permission("user", "user", "update"),
                new Permission("user", "user", "delete"),
                new Permission("user", "user", "assign_clinic"),
        };
        for (Permission perm : userPerms) {
            Permission saved = permissionRepository.save(perm);
            permissionsMap.put(perm.key(), saved);
        }

        // ========== ROLE PERMISSIONS ==========
        Permission[] rolePerms = {
                new Permission("role", "role", "create"),
                new Permission("role", "role", "view"),
                new Permission("role", "role", "update"),
                new Permission("role", "role", "delete"),
                new Permission("role", "role", "assign"),
        };
        for (Permission perm : rolePerms) {
            Permission saved = permissionRepository.save(perm);
            permissionsMap.put(perm.key(), saved);
        }

        // ========== DOCTOR PERMISSIONS (OPS) ==========
        Permission[] doctorPerms = {
                new Permission("ops", "doctor", "create"),
                new Permission("ops", "doctor", "view"),
                new Permission("ops", "doctor", "update"),
                new Permission("ops", "doctor", "delete"),
                new Permission("ops", "doctor", "archive"),
                new Permission("ops", "doctor", "change_status"),
        };
        for (Permission perm : doctorPerms) {
            Permission saved = permissionRepository.save(perm);
            permissionsMap.put(perm.key(), saved);
        }

        // ========== PATIENT PERMISSIONS (OPS) ==========
        Permission[] patientPerms = {
                new Permission("ops", "patient", "create"),
                new Permission("ops", "patient", "view"),
                new Permission("ops", "patient", "update"),
                new Permission("ops", "patient", "delete"),
                new Permission("ops", "patient", "archive"),
        };
        for (Permission perm : patientPerms) {
            Permission saved = permissionRepository.save(perm);
            permissionsMap.put(perm.key(), saved);
        }

        // ========== APPOINTMENT PERMISSIONS (OPS) ==========
        Permission[] appointmentPerms = {
                new Permission("ops", "appointment", "create"),
                new Permission("ops", "appointment", "view"),
                new Permission("ops", "appointment", "update"),
                new Permission("ops", "appointment", "cancel"),
                new Permission("ops", "appointment", "reschedule"),
                new Permission("ops", "appointment", "confirm"),
        };
        for (Permission perm : appointmentPerms) {
            Permission saved = permissionRepository.save(perm);
            permissionsMap.put(perm.key(), saved);
        }

        // ========== AVAILABILITY PERMISSIONS (OPS) ==========
        Permission[] availabilityPerms = {
                new Permission("ops", "availability", "create"),
                new Permission("ops", "availability", "view"),
                new Permission("ops", "availability", "update"),
                new Permission("ops", "availability", "delete"),
                new Permission("ops", "availability", "add_exception"),
                new Permission("ops", "availability", "remove_exception"),
        };
        for (Permission perm : availabilityPerms) {
            Permission saved = permissionRepository.save(perm);
            permissionsMap.put(perm.key(), saved);
        }

        log.info("Created {} permissions", permissionsMap.size());
        return permissionsMap;
    }

    private void createRoles(Map<String, Permission> permissionsMap) {
        if (permissionsMap.isEmpty()) {
            log.warn("Permissions map is empty, cannot create roles");
            return;
        }

        // Check if OWNER role already exists
        var existingOwner = roleRepository.findByName("OWNER");
        if (existingOwner.isPresent()) {
            log.info("OWNER role already exists, skipping role creation");
            return;
        }

        long roleCount = roleRepository.count();
        if (roleCount > 0) {
            log.info("Some roles already exist (count: {}), but OWNER is missing. Creating OWNER role...", roleCount);
        }

        // ========== OWNER ROLE (has all permissions) ==========
        Role ownerRole = new Role();
        ownerRole.setName("OWNER");
        ownerRole.setPermissionIds(new HashSet<>(permissionsMap.values().stream().map(p -> p.getId()).toList()));
        roleRepository.save(ownerRole);
        log.info("Created OWNER role with {} permissions", ownerRole.getPermissionIds().size());

        // ========== ADMIN ROLE ==========
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        Set<String> adminPermIds = new HashSet<>();
        // Clinic permissions
        addPermIfExists(adminPermIds, permissionsMap, "clinic:clinic:view", "clinic:clinic:update");
        // User permissions
        addPermIfExists(adminPermIds, permissionsMap, "user:user:create", "user:user:view", "user:user:update",
                "user:user:assign_clinic");
        // Role permissions
        addPermIfExists(adminPermIds, permissionsMap, "role:role:view", "role:role:assign");
        // Doctor permissions
        addPermIfExists(adminPermIds, permissionsMap, "ops:doctor:view", "ops:doctor:create", "ops:doctor:update",
                "ops:doctor:archive", "ops:doctor:change_status");
        adminRole.setPermissionIds(adminPermIds);
        roleRepository.save(adminRole);
        log.info("Created ADMIN role with {} permissions", adminRole.getPermissionIds().size());

        // ========== DOCTOR ROLE ==========
        Role doctorRole = new Role();
        doctorRole.setName("DOCTOR");
        Set<String> doctorPermIds = new HashSet<>();
        // Doctor can view themselves, patients, appointments, and availability
        addPermIfExists(doctorPermIds, permissionsMap, "ops:doctor:view", "ops:patient:view",
                "ops:appointment:view", "ops:appointment:confirm", "ops:availability:view", "ops:availability:create",
                "ops:availability:update", "ops:availability:add_exception", "ops:availability:remove_exception");
        doctorRole.setPermissionIds(doctorPermIds);
        roleRepository.save(doctorRole);
        log.info("Created DOCTOR role with {} permissions", doctorRole.getPermissionIds().size());

        // ========== RECEPTIONIST ROLE ==========
        Role receptionistRole = new Role();
        receptionistRole.setName("RECEPTIONIST");
        Set<String> receptionistPermIds = new HashSet<>();
        // Receptionist manages patients and appointments
        addPermIfExists(receptionistPermIds, permissionsMap, "ops:patient:create", "ops:patient:view",
                "ops:patient:update", "ops:appointment:create", "ops:appointment:view", "ops:appointment:cancel",
                "ops:appointment:reschedule", "ops:appointment:confirm", "ops:doctor:view");
        receptionistRole.setPermissionIds(receptionistPermIds);
        roleRepository.save(receptionistRole);
        log.info("Created RECEPTIONIST role with {} permissions", receptionistRole.getPermissionIds().size());

        // ========== STAFF ROLE (minimal permissions) ==========
        Role staffRole = new Role();
        staffRole.setName("STAFF");
        Set<String> staffPermIds = new HashSet<>();
        // Staff can only view doctors, patients and appointments
        addPermIfExists(staffPermIds, permissionsMap, "ops:doctor:view", "ops:patient:view", "ops:appointment:view");
        staffRole.setPermissionIds(staffPermIds);
        roleRepository.save(staffRole);
        log.info("Created STAFF role with {} permissions", staffRole.getPermissionIds().size());
    }

    private void addPermIfExists(Set<String> permIds, Map<String, Permission> permissionsMap, String... keys) {
        for (String key : keys) {
            Permission perm = permissionsMap.get(key);
            if (perm != null) {
                permIds.add(perm.getId());
            } else {
                log.warn("Permission not found: {}", key);
            }
        }
    }
}
