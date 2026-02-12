package com.clinicops.modules.clinic.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "clinic_profiles")
@Data
public class ClinicProfile {

    @Id
    private String id;

    private String workspaceId;

    private String name;

    private String address;

    private String phone;

    private String email;

    private Instant createdAt;
    private Instant updatedAt;
}
