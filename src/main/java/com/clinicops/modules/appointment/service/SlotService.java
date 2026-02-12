package com.clinicops.modules.appointment.service;

import java.time.LocalDate;
import java.util.List;

import com.clinicops.modules.appointment.dto.SlotResponse;
import com.clinicops.security.model.AuthUser;

public interface SlotService {

    List<SlotResponse> getAvailableSlots(
        AuthUser user,
        String doctorId,
        LocalDate date,
        int slotMinutes
    );
}

