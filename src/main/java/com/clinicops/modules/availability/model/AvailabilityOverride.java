package com.clinicops.modules.availability.model;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class AvailabilityOverride {

    private LocalDate date;

    private List<TimeRange> availableSlots; // empty = not available

    private String reason;
}
