package com.clinicops.modules.doctor.dto;

import lombok.Data;

@Data
public class DoctorCreateRequest {

    private String userId;

    private String fullName;

    private String registrationNumber;

    private String specialization;
}
