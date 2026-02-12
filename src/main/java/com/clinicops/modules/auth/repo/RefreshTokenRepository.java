package com.clinicops.modules.auth.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.auth.model.RefreshToken;

public interface RefreshTokenRepository
extends MongoRepository<RefreshToken, String> {

Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
}
