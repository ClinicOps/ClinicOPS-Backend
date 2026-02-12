package com.clinicops.modules.appointment.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SlotResponse {
    private Instant startTime;
    private Instant endTime;
}

