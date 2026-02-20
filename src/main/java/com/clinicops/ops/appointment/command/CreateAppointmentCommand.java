package com.clinicops.ops.appointment.command;

import com.clinicops.application.command.Command;

import java.time.Instant;

    public class CreateAppointmentCommand implements Command {

        public final String patientId;
        public final Instant scheduledAt;

        public CreateAppointmentCommand(String patientId, Instant scheduledAt) {
            this.patientId = patientId;
            this.scheduledAt = scheduledAt;
        }

        @Override
        public String domain() { return "OPS"; }

        @Override
        public String resource() { return "APPOINTMENT"; }

        @Override
        public String action() { return "CREATE"; }
    }
