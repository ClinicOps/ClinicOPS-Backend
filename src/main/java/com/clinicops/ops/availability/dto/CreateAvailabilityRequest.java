package com.clinicops.ops.availability.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import org.bson.types.ObjectId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAvailabilityRequest {

	private String clinicId;
    private ObjectId doctorId;

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;
    private LocalTime endTime;

    private Integer slotDurationMinutes;
    private Integer bufferMinutes;

    private LocalDate validFrom;
    private LocalDate validTo;

}
