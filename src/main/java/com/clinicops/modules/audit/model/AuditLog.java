package com.clinicops.modules.audit.model;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "audit_logs")
@Data
@CompoundIndexes({ @CompoundIndex(name = "workspace_time_idx", def = "{'workspaceId':1, 'occurredAt':-1}") })
public class AuditLog {

	@Id
	private String id;

	private String workspaceId;

	private String actorUserId; // who did it
	private String actorRole;

	private String action; // APPOINTMENT_BOOKED, EMR_CREATED, etc
	private String entityType; // APPOINTMENT, VISIT, EMR, INVOICE
	private String entityId;

	private Map<String, Object> metadata; // safe context (no PHI leakage)

	private Instant occurredAt;
}
