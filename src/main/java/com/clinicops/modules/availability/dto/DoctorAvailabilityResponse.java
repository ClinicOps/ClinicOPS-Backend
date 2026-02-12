package com.clinicops.modules.availability.dto;

import java.util.List;

import com.clinicops.modules.availability.model.AvailabilityOverride;
import com.clinicops.modules.availability.model.WeeklyAvailability;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoctorAvailabilityResponse {

    private String doctorId;
    private WeeklyAvailability weekly;
    private List<AvailabilityOverride> overrides;
}
