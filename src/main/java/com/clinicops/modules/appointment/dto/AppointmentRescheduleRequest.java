package com.clinicops.modules.appointment.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class AppointmentRescheduleRequest {

    private Instant newStartTime;
    private Instant newEndTime;
}

