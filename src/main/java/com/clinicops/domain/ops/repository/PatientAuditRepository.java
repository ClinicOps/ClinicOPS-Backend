package com.clinicops.domain.ops.repository;

import com.clinicops.domain.ops.model.PatientAudit;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientAuditRepository extends MongoRepository<PatientAudit, ObjectId> {
}
