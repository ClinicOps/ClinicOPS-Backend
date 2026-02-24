package com.clinicops.domain.access.model;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    private ObjectId id;

    private ObjectId userId;

    private String tokenHash;

    private Instant expiryDate;

    private boolean revoked;
}
