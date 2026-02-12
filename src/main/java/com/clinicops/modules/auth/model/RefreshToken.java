package com.clinicops.modules.auth.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    private String id;

    private String userId;

    private String tokenHash;

    private Instant expiresAt;

    private boolean revoked;
}
