package com.clinicops.common.audit;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.bson.types.ObjectId;

public interface AuditLogRepository
        extends MongoRepository<AuditLog, ObjectId> {
}