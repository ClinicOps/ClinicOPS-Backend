package com.clinicops.modules.availability.model;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeRange {
    private LocalTime start;
    private LocalTime end;
}
