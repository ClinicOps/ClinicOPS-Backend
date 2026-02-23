package com.clinicops.ops.availability.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.ops.availability.model.DoctorAvailabilityExceptionAudit;

public interface DoctorAvailabilityExceptionAuditRepository
		extends MongoRepository<DoctorAvailabilityExceptionAudit, ObjectId> {
}
