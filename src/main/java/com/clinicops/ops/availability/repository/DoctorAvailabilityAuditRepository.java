package com.clinicops.ops.availability.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.ops.availability.model.DoctorAvailabilityAudit;

public interface DoctorAvailabilityAuditRepository extends MongoRepository<DoctorAvailabilityAudit, ObjectId> {
}
