package com.clinicops.modules.department.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.common.exception.NotFoundException;
import com.clinicops.modules.department.dto.DepartmentCreateRequest;
import com.clinicops.modules.department.dto.DepartmentResponse;
import com.clinicops.modules.department.dto.SpecializationRequest;
import com.clinicops.modules.department.dto.SpecializationResponse;
import com.clinicops.modules.department.model.Department;
import com.clinicops.modules.department.model.Specialization;
import com.clinicops.modules.department.repo.DepartmentRepository;
import com.clinicops.security.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repository;

    @Override
    public DepartmentResponse createDepartment(
            AuthUser user,
            DepartmentCreateRequest request) {

        Department dept = new Department();
        dept.setWorkspaceId(user.getWorkspaceId());
        dept.setName(request.getName());
        dept.setCreatedAt(Instant.now());

        repository.save(dept);
        return toResponse(dept);
    }

    @Override
    public DepartmentResponse addSpecialization(
            AuthUser user,
            String departmentId,
            SpecializationRequest request) {

        Department dept = repository.findById(departmentId)
            .orElseThrow(() -> new NotFoundException("Department not found"));

        if (!dept.getWorkspaceId().equals(user.getWorkspaceId())) {
            throw new BusinessException("Cross-workspace access denied");
        }

        Specialization spec = new Specialization();
        spec.setId(UUID.randomUUID().toString());
        spec.setName(request.getName());
        spec.setActive(true);

        dept.getSpecializations().add(spec);
        repository.save(dept);

        return toResponse(dept);
    }

    @Override
    public List<DepartmentResponse> listDepartments(AuthUser user) {

        return repository.findByWorkspaceId(user.getWorkspaceId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private DepartmentResponse toResponse(Department dept) {

        List<SpecializationResponse> specs =
            dept.getSpecializations().stream()
                .map(s -> new SpecializationResponse(
                        s.getId(),
                        s.getName(),
                        s.isActive()
                ))
                .toList();

        return new DepartmentResponse(
            dept.getId(),
            dept.getName(),
            specs
        );
    }
}

