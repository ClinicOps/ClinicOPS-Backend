package com.clinicops.ops.doctor.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.ops.doctor.model.ClinicDoctor;

public interface ClinicDoctorRepository extends MongoRepository<ClinicDoctor, ObjectId> {

    Optional<ClinicDoctor> findByClinicIdAndDoctorId(
        ObjectId clinicId, ObjectId doctorId);

    Page<ClinicDoctor> findByClinicIdAndArchivedFalse(
        ObjectId clinicId, Pageable pageable);

    boolean existsByClinicIdAndDoctorId(
        ObjectId clinicId, ObjectId doctorId);
}
