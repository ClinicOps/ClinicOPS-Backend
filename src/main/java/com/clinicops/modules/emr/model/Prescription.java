package com.clinicops.modules.emr.model;

import lombok.Data;

@Data
public class Prescription {

    private Medication medication;

    private String dosage;      // "500 mg"
    private String frequency;   // "Twice daily"
    private int durationDays;   // 5
    private String instructions;
}
