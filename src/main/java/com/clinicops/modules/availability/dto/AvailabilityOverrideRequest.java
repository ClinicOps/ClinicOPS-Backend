package com.clinicops.modules.availability.dto;

import java.time.LocalDate;
import java.util.List;

import com.clinicops.modules.availability.model.TimeRange;

import lombok.Data;

@Data
public class AvailabilityOverrideRequest {

    private LocalDate date;

    private List<TimeRange> availableSlots;

    private String reason;
}

