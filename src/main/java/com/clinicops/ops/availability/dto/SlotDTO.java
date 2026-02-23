package com.clinicops.ops.availability.dto;

import java.time.LocalTime;

import com.clinicops.ops.availability.model.SlotStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlotDTO {

    private LocalTime start;
    private LocalTime end;
    private SlotStatus status;
}
