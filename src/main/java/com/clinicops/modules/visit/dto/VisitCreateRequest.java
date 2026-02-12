package com.clinicops.modules.visit.dto;

import lombok.Data;

@Data
public class VisitCreateRequest {

    private String patientId;

    private String doctorId;

    private String appointmentId; // optional

    private String reason;
}

