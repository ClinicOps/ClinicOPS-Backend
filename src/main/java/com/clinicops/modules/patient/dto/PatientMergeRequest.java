package com.clinicops.modules.patient.dto;

import lombok.Data;

@Data
public class PatientMergeRequest {

    private String sourcePatientId;
    private String targetPatientId;
}
