package com.clinicops.modules.availability.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.availability.model.DoctorAvailability;

public interface DoctorAvailabilityRepository
extends MongoRepository<DoctorAvailability, String> {

Optional<DoctorAvailability> findByDoctorId(String doctorId);
}
