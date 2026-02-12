package com.clinicops.modules.appointment.dto;

import lombok.Data;

@Data
public class AppointmentCancelRequest {
    private String reason; // optional, for audit
}
