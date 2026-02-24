package com.clinicops.domain.access.repository;

import com.clinicops.domain.access.model.RefreshToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends MongoRepository<RefreshToken, ObjectId> {

    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
}