package com.clinicops.domain.access.model;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "refresh_tokens")
@Data
@CompoundIndex(name = "refresh_token_hash_idx", def = "{'tokenHash':1}", unique = true)
public class RefreshToken {

    @Id
    private ObjectId id;

    private ObjectId userId;

    private String tokenHash;

    private Instant expiryDate;

    private boolean revoked;
}
