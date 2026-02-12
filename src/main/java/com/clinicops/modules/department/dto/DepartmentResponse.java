package com.clinicops.modules.department.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DepartmentResponse {
    private String id;
    private String name;
    private List<SpecializationResponse> specializations;
}