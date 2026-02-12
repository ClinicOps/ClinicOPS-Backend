package com.clinicops.modules.doctor.api;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.modules.doctor.dto.DoctorCreateRequest;
import com.clinicops.modules.doctor.dto.DoctorResponse;
import com.clinicops.modules.doctor.service.DoctorService;
import com.clinicops.security.annotation.RequirePermission;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @RequirePermission(Permission.DOCTOR_MANAGE)
    public ApiResponse<DoctorResponse> registerDoctor(
            @RequestBody DoctorCreateRequest request) {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            doctorService.registerDoctor(user, request)
        );
    }

    @GetMapping
    @RequirePermission(Permission.DOCTOR_READ)
    public ApiResponse<List<DoctorResponse>> listDoctors() {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            doctorService.listDoctors(user)
        );
    }
}

