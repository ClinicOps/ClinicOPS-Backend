package com.clinicops.infra.messaging.events;

import com.clinicops.infra.messaging.BaseEvent;

import lombok.Getter;

@Getter
public class AppointmentCancelledEvent extends BaseEvent {

    private final String appointmentId;
    private final String workspaceId;
    private final String doctorId;
    private final String patientId;

    public AppointmentCancelledEvent(
            String appointmentId,
            String workspaceId,
            String doctorId,
            String patientId) {

        this.appointmentId = appointmentId;
        this.workspaceId = workspaceId;
        this.doctorId = doctorId;
        this.patientId = patientId;
    }

    // getters
    
}
