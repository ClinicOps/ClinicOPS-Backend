package com.clinicops.ops.doctor.dto;

import java.time.LocalDate;

import com.clinicops.ops.doctor.model.DoctorStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeDoctorStatusRequest {

    @NotNull
    private DoctorStatus status;

    private LocalDate visitingFrom;
    private LocalDate visitingTo;
}
