package com.clinicops.modules.auth.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.auth.model.User;
import com.clinicops.modules.auth.model.UserStatus;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmailAndStatus(String email, UserStatus status);
}
