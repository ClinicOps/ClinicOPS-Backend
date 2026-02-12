package com.clinicops.modules.availability.api;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.modules.availability.dto.AvailabilityOverrideRequest;
import com.clinicops.modules.availability.dto.DoctorAvailabilityResponse;
import com.clinicops.modules.availability.dto.WeeklyAvailabilityRequest;
import com.clinicops.modules.availability.service.AvailabilityService;
import com.clinicops.security.annotation.RequirePermission;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/doctors/{doctorId}/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService service;

    @GetMapping
    @RequirePermission(Permission.DOCTOR_MANAGE)
    public ApiResponse<DoctorAvailabilityResponse> get(
            @PathVariable String doctorId) {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            service.getAvailability(user, doctorId)
        );
    }

    @PostMapping("/weekly")
    @RequirePermission(Permission.DOCTOR_MANAGE)
    public ApiResponse<DoctorAvailabilityResponse> setWeekly(
            @PathVariable String doctorId,
            @RequestBody WeeklyAvailabilityRequest request) {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            service.setWeeklyAvailability(user, doctorId, request)
        );
    }

    @PostMapping("/overrides")
    @RequirePermission(Permission.DOCTOR_MANAGE)
    public ApiResponse<DoctorAvailabilityResponse> addOverride(
            @PathVariable String doctorId,
            @RequestBody AvailabilityOverrideRequest request) {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            service.addOverride(user, doctorId, request)
        );
    }
}
