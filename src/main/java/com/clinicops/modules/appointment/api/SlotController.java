package com.clinicops.modules.appointment.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.modules.appointment.dto.SlotResponse;
import com.clinicops.modules.appointment.service.SlotService;
import com.clinicops.security.annotation.RequirePermission;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/doctors/{doctorId}/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @GetMapping
    @RequirePermission(Permission.APPOINTMENT_READ)
    public ApiResponse<List<SlotResponse>> getSlots(
            @PathVariable String doctorId,
            @RequestParam String date,
            @RequestParam(defaultValue = "15") int durationMinutes) {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        LocalDate localDate = LocalDate.parse(date);

        return ApiResponse.ok(
            slotService.getAvailableSlots(
                user, doctorId, localDate, durationMinutes
            )
        );
    }
}
