package com.clinicops.application.command;

import com.clinicops.domain.access.service.PermissionEvaluator;
import com.clinicops.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CommandGateway {

    private final PermissionEvaluator evaluator;

    public CommandGateway(PermissionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public void execute(
            Command command,
            HttpServletRequest request,
            CommandHandler handler) {

        AuthenticatedUser user =
                (AuthenticatedUser) request.getAttribute("AUTH_USER");
        String clinicId =
                (String) request.getAttribute("CLINIC_ID");

        boolean allowed = evaluator.isAllowed(
                user.getUserId(),
                clinicId,
                command.domain(),
                command.resource(),
                command.action()
        );

        if (!allowed) {
            throw new RuntimeException("Forbidden");
        }

        handler.handle(command);
        // audit hook comes here
    }
}
