package com.clinicops.modules.audit.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.audit.model.AuditLog;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

}