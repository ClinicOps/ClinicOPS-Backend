package com.clinicops.ops.patient.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.ops.patient.model.PatientAudit;

public interface PatientAuditRepository extends MongoRepository<PatientAudit, ObjectId> {
}
