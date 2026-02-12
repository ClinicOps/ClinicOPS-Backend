package com.clinicops.modules.auth.model;

import java.time.Instant;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;

    private String workspaceId;

    private String email;

    private String passwordHash;

    private Set<String> roles;

    private UserStatus status;

    private Instant createdAt;
}