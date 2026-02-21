package com.clinicops.ops.doctor.dto;

import java.util.List;

import com.clinicops.ops.doctor.model.DoctorStatus;

import lombok.Data;

@Data
public class DoctorResponse {

    private String id;
    private String clinicDoctorId;

    private String firstName;
    private String lastName;
    private String licenseNumber;

    private String profileImageUrl;

    private List<String> specializations;

    private Integer consultationFee;

    private DoctorStatus status;
    private Boolean available;

    private Boolean archived;
}