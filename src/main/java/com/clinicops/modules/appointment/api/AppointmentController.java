package com.clinicops.modules.appointment.api;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.modules.appointment.dto.AppointmentCancelRequest;
import com.clinicops.modules.appointment.dto.AppointmentCreateRequest;
import com.clinicops.modules.appointment.dto.AppointmentRescheduleRequest;
import com.clinicops.modules.appointment.dto.AppointmentResponse;
import com.clinicops.modules.appointment.service.AppointmentService;
import com.clinicops.security.annotation.RequirePermission;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    @RequirePermission(Permission.APPOINTMENT_BOOK)
    public ApiResponse<AppointmentResponse> book(
            @RequestBody AppointmentCreateRequest request) {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            service.bookAppointment(user, request)
        );
    }

    @PostMapping("/{id}/cancel")
    @RequirePermission(Permission.APPOINTMENT_BOOK)
    public ApiResponse<?> cancel(
            @PathVariable String id,
            @RequestBody AppointmentCancelRequest request) {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        service.cancelAppointment(user, id, request);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{id}/reschedule")
    @RequirePermission(Permission.APPOINTMENT_BOOK)
    public ApiResponse<AppointmentResponse> reschedule(
            @PathVariable String id,
            @RequestBody AppointmentRescheduleRequest request) {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            service.rescheduleAppointment(user, id, request)
        );
    }
}

