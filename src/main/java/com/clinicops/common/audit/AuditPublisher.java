package com.clinicops.common.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditPublisher {
	
	private final AuditLogRepository auditLogRepository;
        // For now: log to console or persist to Mongo
        // Later: send to RabbitMQ / Elastic / analytics pipeline
        
        public void publish(AuditRecord record) {
        	System.out.println("AUDIT -> " + record);

            AuditLog log = AuditLog.builder()
                    .userId(record.getUserId())
                    .clinicId(record.getClinicId())
                    .domain(record.getDomain())
                    .resource(record.getResource())
                    .action(record.getAction())
                    .timestamp(record.getTimestamp())
                    .metadata(record.getMetadata())
                    .build();

            auditLogRepository.save(log);
    }
}