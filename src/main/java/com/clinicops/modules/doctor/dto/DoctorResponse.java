package com.clinicops.modules.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoctorResponse {

    private String id;
    private String fullName;
    private String specialization;
    private boolean active;
}
