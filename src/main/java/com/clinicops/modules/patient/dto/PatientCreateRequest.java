package com.clinicops.modules.patient.dto;

import lombok.Data;

@Data
public class PatientCreateRequest {

    private String fullName;
    private String phone;
    private String email;
    private Integer age;
    private String gender;
}

