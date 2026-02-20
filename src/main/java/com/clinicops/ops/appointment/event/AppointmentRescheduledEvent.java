package com.clinicops.ops.appointment.event;

import com.clinicops.infra.messaging.BaseEvent;

import lombok.Getter;

@Getter
public class AppointmentRescheduledEvent extends BaseEvent {

    private final String oldAppointmentId;
    private final String newAppointmentId;
    private final String workspaceId;

    public AppointmentRescheduledEvent(
            String oldAppointmentId,
            String newAppointmentId,
            String workspaceId) {

        this.oldAppointmentId = oldAppointmentId;
        this.newAppointmentId = newAppointmentId;
        this.workspaceId = workspaceId;
    }

    // getters
}
