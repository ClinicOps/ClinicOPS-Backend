package com.clinicops.modules.audit.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clinicops.modules.audit.model.AuditLog;
import com.clinicops.modules.audit.repo.AuditLogRepository;
import com.clinicops.security.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

	private final AuditLogRepository repo;

	@Override
	public void record(String workspaceId, AuthUser actor, String action, String entityType, String entityId,
			Map<String, Object> metadata) {

		AuditLog log = new AuditLog();
		log.setWorkspaceId(workspaceId);
		log.setActorUserId(actor.getUserId());
		log.setActorRole(String.join(",", actor.getRoles()));
		log.setAction(action);
		log.setEntityType(entityType);
		log.setEntityId(entityId);
		log.setMetadata(metadata);
		log.setOccurredAt(Instant.now());

		repo.save(log);
	}
}
