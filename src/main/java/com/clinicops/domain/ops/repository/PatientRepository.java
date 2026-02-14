package com.clinicops.domain.ops.repository;

import com.clinicops.domain.ops.model.Patient;
import com.clinicops.domain.ops.model.PatientStatus;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PatientRepository extends MongoRepository<Patient, ObjectId> {

	Page<Patient> findByClinicIdAndStatusNot(ObjectId clinicId, PatientStatus status, Pageable pageable);

	Optional<Patient> findByClinicIdAndId(ObjectId clinicId, ObjectId id);

	boolean existsByClinicIdAndContactMobile(ObjectId clinicId, String mobile);
}
