package com.clinicops.modules.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientResponse {

    private String id;
    private String fullName;
    private String phone;
    private Integer age;
    private String gender;
}

