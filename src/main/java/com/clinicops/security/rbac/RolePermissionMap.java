package com.clinicops.security.rbac;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class RolePermissionMap {

    private static final Map<Role, Set<Permission>> MAP = Map.of(
        Role.OWNER, Set.of(
            Permission.PATIENT_CREATE,
            Permission.PATIENT_READ,
            Permission.CLINIC_MANAGE,
            Permission.DOCTOR_MANAGE,
            Permission.DOCTOR_READ,
            Permission.DEPARTMENT_MANAGE, 
            Permission.DEPARTMENT_READ,
            Permission.APPOINTMENT_READ,
            Permission.APPOINTMENT_BOOK,
            Permission.EMR_READ,
            Permission.BILLING_READ,
            Permission.BILLING_WRITE,
            Permission.ADMIN_READ, 
            Permission.ANALYTICS_READ, 
            Permission.WORKSPACES_READ
            
        ),
        Role.DOCTOR, Set.of(
            Permission.PATIENT_READ,
            Permission.DOCTOR_READ,
            Permission.DEPARTMENT_READ,
            Permission.APPOINTMENT_READ,
            Permission.APPOINTMENT_BOOK,
            Permission.EMR_READ,
            Permission.EMR_WRITE,
            Permission.BILLING_READ,
            Permission.WORKSPACES_READ
         

        ),
        Role.STAFF, Set.of(
            Permission.PATIENT_CREATE,
            Permission.PATIENT_READ,
            Permission.DOCTOR_READ,
            Permission.DEPARTMENT_READ,
            Permission.APPOINTMENT_READ,      
            Permission.EMR_READ,
            Permission.BILLING_READ,
            Permission.BILLING_WRITE,
            Permission.ANALYTICS_READ,
            Permission.WORKSPACES_READ
          
        ),
        Role.OPS, Set.of(
                Permission.OPS_EXECUTE    //(future role)         
            )
    );

    public Set<Permission> getPermissions(Set<String> roles) {
        return roles.stream()
                .map(Role::valueOf)
                .flatMap(r -> MAP.get(r).stream())
                .collect(Collectors.toSet());
    }
}
