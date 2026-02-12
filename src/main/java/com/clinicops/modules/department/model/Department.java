package com.clinicops.modules.department.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "departments")
@Data
public class Department {

    @Id
    private String id;

    private String workspaceId;

    private String name;

    private List<Specialization> specializations = new ArrayList<>();

    private Instant createdAt;
}
