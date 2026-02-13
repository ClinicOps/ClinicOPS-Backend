package com.clinicops.application.command.appointment;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.domain.ops.model.Appointment;
import com.clinicops.domain.ops.service.AppointmentService;
import org.springframework.stereotype.Component;

@Component
public class CancelAppointmentHandler
        implements CommandHandler<CancelAppointmentCommand> {

    private final AppointmentService service;

    public CancelAppointmentHandler(AppointmentService service) {
        this.service = service;
    }

    @Override
    public void handle(CancelAppointmentCommand command) {
        throw new UnsupportedOperationException();
    }

    public void handle(
            CancelAppointmentCommand command,
            String clinicId) {

        Appointment appt = service.get(command.appointmentId);

        if (!appt.getClinicId().equals(clinicId)) {
            throw new RuntimeException("Cross-clinic access denied");
        }

        service.cancel(appt);
    }
}
