package com.clinicops.modules.department.service;

import java.util.List;

import com.clinicops.modules.department.dto.DepartmentCreateRequest;
import com.clinicops.modules.department.dto.DepartmentResponse;
import com.clinicops.modules.department.dto.SpecializationRequest;
import com.clinicops.security.model.AuthUser;

public interface DepartmentService {

    DepartmentResponse createDepartment(
        AuthUser user,
        DepartmentCreateRequest request
    );

    DepartmentResponse addSpecialization(
        AuthUser user,
        String departmentId,
        SpecializationRequest request
    );

    List<DepartmentResponse> listDepartments(AuthUser user);
}
