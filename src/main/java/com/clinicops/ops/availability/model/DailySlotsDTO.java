package com.clinicops.ops.availability.model;

import java.time.LocalDate;
import java.util.List;

import com.clinicops.ops.availability.dto.SlotDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailySlotsDTO {

    private LocalDate date;
    private List<SlotDTO> slots;

    public DailySlotsDTO(LocalDate date, List<SlotDTO> slots) {
        this.date = date;
        this.slots = slots;
    }
}
