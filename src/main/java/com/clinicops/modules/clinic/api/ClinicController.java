package com.clinicops.modules.clinic.api;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.modules.clinic.dto.ClinicProfileRequest;
import com.clinicops.modules.clinic.dto.ClinicProfileResponse;
import com.clinicops.modules.clinic.service.ClinicService;
import com.clinicops.security.annotation.RequirePermission;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/clinic")
@RequiredArgsConstructor
@RequirePermission(Permission.CLINIC_MANAGE)
public class ClinicController {

    private final ClinicService clinicService;

    @GetMapping("/profile")
    public ApiResponse<ClinicProfileResponse> getProfile() {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            clinicService.getProfile(user)
        );
    }

    @PostMapping("/profile")
    public ApiResponse<ClinicProfileResponse> upsertProfile(
            @RequestBody ClinicProfileRequest request) {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            clinicService.upsertProfile(user, request)
        );
    }
}

