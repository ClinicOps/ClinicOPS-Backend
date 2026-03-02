package com.clinicops.ops.appointment.command;

import com.clinicops.application.command.CommandHandler;
import com.clinicops.ops.appointment.model.Appointment;
import com.clinicops.ops.appointment.service.AppointmentService;

import org.bson.types.ObjectId;
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
            ObjectId objectId) {

        Appointment appt = service.get(command.appointmentId);

        if (!appt.getClinicId().toString().equals(objectId)) {
            throw new RuntimeException("Cross-clinic access denied");
        }

        service.cancel(appt);
    }
}
