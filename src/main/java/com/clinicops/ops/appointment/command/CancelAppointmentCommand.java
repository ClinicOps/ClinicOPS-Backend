package com.clinicops.ops.appointment.command;

import com.clinicops.application.command.Command;

public class CancelAppointmentCommand implements Command {

    public final String appointmentId;

    public CancelAppointmentCommand(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    @Override
    public String domain() {
        return "OPS";
    }

    @Override
    public String resource() {
        return "APPOINTMENT";
    }

    @Override
    public String action() {
        return "UPDATE";
    }
}
