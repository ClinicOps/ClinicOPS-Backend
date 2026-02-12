package com.clinicops.modules.availability.dto;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

import com.clinicops.modules.availability.model.TimeRange;

import lombok.Data;

@Data
public class WeeklyAvailabilityRequest {

    private Map<DayOfWeek, List<TimeRange>> days;
}
