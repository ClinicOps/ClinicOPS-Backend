package com.clinicops.ops.doctor.dto;

import java.util.List;

import lombok.Data;

@Data
public class UpdateDoctorRequest {

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private List<String> qualifications;
    private String profileImageUrl;

    private List<String> specializations;
    private Integer consultationFee;

    private Boolean available;
}
