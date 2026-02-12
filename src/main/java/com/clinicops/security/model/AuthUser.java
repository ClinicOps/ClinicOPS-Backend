package com.clinicops.security.model;

import java.util.Set;

import com.clinicops.security.rbac.Permission;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthUser {
    private String userId;
    private String workspaceId;
    private Set<String> roles;          
    private Set<Permission> permissions;
}
