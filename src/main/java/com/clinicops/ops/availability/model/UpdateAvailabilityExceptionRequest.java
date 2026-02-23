package com.clinicops.ops.availability.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAvailabilityExceptionRequest {

    private LocalDate date;

    private LocalTime startTime;
    private LocalTime endTime;

    private ExceptionType type;

    private String reason;

    private Long version;
}