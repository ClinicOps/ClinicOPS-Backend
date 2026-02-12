package com.clinicops.modules.department.api;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.modules.department.dto.DepartmentCreateRequest;
import com.clinicops.modules.department.dto.DepartmentResponse;
import com.clinicops.modules.department.dto.SpecializationRequest;
import com.clinicops.modules.department.service.DepartmentService;
import com.clinicops.security.annotation.RequirePermission;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    @PostMapping
    @RequirePermission(Permission.DEPARTMENT_MANAGE)
    public ApiResponse<DepartmentResponse> create(
            @RequestBody DepartmentCreateRequest request) {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            service.createDepartment(user, request)
        );
    }

    @PostMapping("/{id}/specializations")
    @RequirePermission(Permission.DEPARTMENT_MANAGE)
    public ApiResponse<DepartmentResponse> addSpecialization(
            @PathVariable String id,
            @RequestBody SpecializationRequest request) {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            service.addSpecialization(user, id, request)
        );
    }

    @GetMapping
    @RequirePermission(Permission.DEPARTMENT_READ)
    public ApiResponse<List<DepartmentResponse>> list() {

        AuthUser user =
            (AuthUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ApiResponse.ok(
            service.listDepartments(user)
        );
    }
}

