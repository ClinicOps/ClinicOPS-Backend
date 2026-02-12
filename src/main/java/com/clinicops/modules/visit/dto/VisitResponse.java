package com.clinicops.modules.visit.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VisitResponse {

    private String id;
    private String patientId;
    private String doctorId;
    private Instant visitTime;
    private String reason;
}

