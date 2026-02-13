package com.clinicops.application.command.appointment;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.domain.ops.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class CreateAppointmentHandler
        implements CommandHandler<CreateAppointmentCommand> {

    private final AppointmentService service;

    public CreateAppointmentHandler(AppointmentService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateAppointmentCommand command) {
        throw new UnsupportedOperationException(
            "Use overloaded handler with clinic context");
    }

    public void handle(
            CreateAppointmentCommand command,
            String clinicId) {

        service.create(
                clinicId,
                command.patientName,
                command.scheduledAt
        );
    }
}
