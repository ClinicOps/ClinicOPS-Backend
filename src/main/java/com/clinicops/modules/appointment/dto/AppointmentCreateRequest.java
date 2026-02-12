package com.clinicops.modules.appointment.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class AppointmentCreateRequest {

    private String doctorId;

    private String patientId;

    private Instant startTime;

    private Instant endTime;
}
