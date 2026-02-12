package com.clinicops.modules.audit.service;

import java.util.Map;

import com.clinicops.security.model.AuthUser;

public interface AuditService {

	void record(String workspaceId, AuthUser actor, String action, String entityType, String entityId,
			Map<String, Object> metadata);
}
