package com.clinicops.modules.department.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpecializationResponse {
    private String id;
    private String name;
    private boolean active;
}
