package com.clinicops.domain.access.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document("user_role_assignments")
@Data
public class UserRoleAssignment {

    @Id
    private String id;

    private String userId;
    private String clinicId;
    private String roleId;
    private String status; // ACTIVE / SUSPENDED
}
