package com.clinicops.infra.messaging.events;

import java.time.Instant;

import com.clinicops.infra.messaging.BaseEvent;

import lombok.Getter;

@Getter
public class AppointmentBookedEvent extends BaseEvent {

    private final String appointmentId;
    private final String workspaceId;
    private final String doctorId;
    private final String patientId;
    private final Instant startTime;
    private final Instant endTime;

    public AppointmentBookedEvent(
            String appointmentId,
            String workspaceId,
            String doctorId,
            String patientId,
            Instant startTime,
            Instant endTime) {

        this.appointmentId = appointmentId;
        this.workspaceId = workspaceId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // getters
    
}
