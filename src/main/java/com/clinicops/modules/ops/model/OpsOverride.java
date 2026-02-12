package com.clinicops.modules.ops.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "ops_overrides")
@Data
public class OpsOverride {

	@Id
	private String id;

	private String workspaceId;

	private String performedByUserId;

	private String action; // FORCE_CANCEL_APPOINTMENT
	private String targetType; // APPOINTMENT
	private String targetId;

	private String reason;

	private Instant performedAt;
}
