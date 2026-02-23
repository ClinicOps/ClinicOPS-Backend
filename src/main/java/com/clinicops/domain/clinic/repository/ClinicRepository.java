package com.clinicops.domain.clinic.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.domain.clinic.model.Clinic;

public interface ClinicRepository extends MongoRepository<Clinic, ObjectId> {

    Optional<Clinic> findByCodeAndDeletedFalse(String code);

    boolean existsByCode(String code);

	boolean existsByCodeAndDeletedFalse(String slug);
}
