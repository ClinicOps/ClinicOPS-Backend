package com.clinicops.modules.emr.dto;

import lombok.Data;

@Data
public class DiagnosisRequest {
    private String code;
    private String description;
    private boolean primary;
}

