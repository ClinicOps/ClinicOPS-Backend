package com.clinicops.modules.emr.model;

import lombok.Data;

@Data
public class Diagnosis {

    private String code;        // ICD-friendly
    private String description; // Human-readable
    private boolean primary;
}
