package com.clinicops.ops.availability.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.bson.types.ObjectId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAvailabilityExceptionRequest {

    private ObjectId doctorId;

    private LocalDate date;

    private LocalTime startTime;   // nullable = full day
    private LocalTime endTime;     // nullable = full day

    private ExceptionType type;    // BLOCKED, EXTRA, LEAVE

    private String reason;
}
