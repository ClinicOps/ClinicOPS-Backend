package com.clinicops.security.aspect;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.clinicops.security.annotation.RequirePermission;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;

@Aspect
@Component
public class PermissionAspect {

    @Before("@within(requirePermission) || @annotation(requirePermission)")
    public void checkPermission(RequirePermission requirePermission) {

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUser)) {
            throw new AccessDeniedException("Unauthorized");
        }

        AuthUser user = (AuthUser) authentication.getPrincipal();

        Permission required = requirePermission.value();

        if (!user.getPermissions().contains(required)) {
            throw new AccessDeniedException("Missing permission: " + required);
        }
    }
}

