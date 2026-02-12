package com.clinicops.modules.availability.model;

import java.time.*;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeeklyAvailability {

    // MONDAY → SUNDAY
    private Map<DayOfWeek, List<TimeRange>> days = new EnumMap<>(DayOfWeek.class);
}
