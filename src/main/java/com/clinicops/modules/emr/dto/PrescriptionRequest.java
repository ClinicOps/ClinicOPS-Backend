package com.clinicops.modules.emr.dto;

import lombok.Data;

@Data
public class PrescriptionRequest {

    private String medicationName;
    private String strength;
    private String dosage;
    private String frequency;
    private int durationDays;
    private String instructions;
}
