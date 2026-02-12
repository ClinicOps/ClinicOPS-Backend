package com.clinicops.modules.clinic.dto;

import lombok.Data;

@Data
public class ClinicProfileRequest {
    private String name;
    private String address;
    private String phone;
    private String email;
}
