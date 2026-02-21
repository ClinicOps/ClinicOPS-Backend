package com.clinicops.ops.doctor.dto;

import java.time.LocalDate;
import java.util.List;

import com.clinicops.ops.doctor.model.DoctorStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDoctorRequest {

    @NotBlank
    private String licenseNumber;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String phone;
    private String email;

    private List<String> qualifications;

    private String profileImageUrl;

    @NotEmpty
    private List<String> specializations;

    @NotNull
    private Integer consultationFee;

    @NotNull
    private DoctorStatus status;

    private LocalDate visitingFrom;
    private LocalDate visitingTo;
}
