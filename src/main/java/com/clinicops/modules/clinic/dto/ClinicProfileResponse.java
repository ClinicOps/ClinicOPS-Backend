package com.clinicops.modules.clinic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClinicProfileResponse {
    private String name;
    private String address;
    private String phone;
    private String email;
}
