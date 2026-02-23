package com.clinicops.ops.availability.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAvailabilityRequest {

	private String clinicId;
    private DayOfWeek dayOfWeek;

    private LocalTime startTime;
    private LocalTime endTime;

    private Integer slotDurationMinutes;
    private Integer bufferMinutes;

    private LocalDate validFrom;
    private LocalDate validTo;

    private Long version;
}
