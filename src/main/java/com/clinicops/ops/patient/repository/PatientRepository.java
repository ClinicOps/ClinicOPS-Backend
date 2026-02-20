package com.clinicops.ops.patient.repository;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.ops.patient.model.Patient;
import com.clinicops.ops.patient.model.PatientStatus;

import java.util.Optional;

public interface PatientRepository extends MongoRepository<Patient, ObjectId> {

	Page<Patient> findByClinicIdAndStatusNot(ObjectId clinicId, PatientStatus status, Pageable pageable);

	Optional<Patient> findByClinicIdAndId(ObjectId clinicId, ObjectId id);

	boolean existsByClinicIdAndContactMobile(ObjectId clinicId, String mobile);
}
