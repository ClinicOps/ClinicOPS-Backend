package com.clinicops.ops.doctor.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.ops.doctor.model.Doctor;

public interface DoctorRepository extends MongoRepository<Doctor, ObjectId> {

    Optional<Doctor> findByLicenseNumber(String licenseNumber);
}
