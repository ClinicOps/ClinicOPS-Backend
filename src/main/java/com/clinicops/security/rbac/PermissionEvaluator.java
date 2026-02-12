package com.clinicops.security.rbac;

import org.springframework.stereotype.Component;

import com.clinicops.security.model.AuthUser;

@Component
public class PermissionEvaluator {

    @SuppressWarnings("unlikely-arg-type")
	public boolean hasPermission(AuthUser user, Permission permission) {
        return user.getPermissions().contains(permission.name());
    }
}