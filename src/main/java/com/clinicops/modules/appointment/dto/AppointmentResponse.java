package com.clinicops.modules.appointment.dto;

import java.time.Instant;

import com.clinicops.modules.appointment.model.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppointmentResponse {

    private String id;
    private String doctorId;
    private String patientId;
    private Instant startTime;
    private Instant endTime;
    private AppointmentStatus status;
}
