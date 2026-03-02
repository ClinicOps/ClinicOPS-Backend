package com.clinicops.application.command;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.clinicops.common.audit.AuditPublisher;
import com.clinicops.common.audit.AuditRecord;
import com.clinicops.common.exception.AuthorizationException;
import com.clinicops.domain.access.service.PermissionEvaluator;
import com.clinicops.security.AuthenticatedUser;
import com.clinicops.security.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommandGateway {

    private final PermissionEvaluator evaluator;
    private final AuditPublisher auditPublisher;


    public CommandGateway(PermissionEvaluator evaluator, AuditPublisher auditPublisher) {
        this.evaluator = evaluator;
        this.auditPublisher = auditPublisher;
    }

    public <C extends Command> void execute(
            C command,
            HttpServletRequest request,
            CommandHandler<C> handler) throws AuthorizationException {

        ObjectId userId = SecurityUtils.getCurrentUserId();
        ObjectId clinicId = SecurityUtils.getCurrentClinicId();

        boolean allowed = evaluator.isAllowed(
        		userId,
                clinicId,
                command.domain(),
                command.resource(),
                command.action()
        );

        if (!allowed) {
            throw new AuthorizationException("Forbidden");
        }

        handler.handle(command);
        
     // Publish audit AFTER success
        publishAudit(userId, clinicId, command);
    }
    
    private void publishAudit(
            ObjectId userId,
            ObjectId clinicId,
            Command command) {

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("commandClass", command.getClass().getSimpleName());

        AuditRecord record = AuditRecord.builder()
                .userId(userId)
                .clinicId(clinicId)
                .domain(command.domain())
                .resource(command.resource())
                .action(command.action())
                .timestamp(Instant.now())
                .metadata(metadata)
                .build();

        auditPublisher.publish(record);
    }
}
