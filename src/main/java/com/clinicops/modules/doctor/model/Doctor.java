package com.clinicops.modules.doctor.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "doctors")
@Data
public class Doctor {

    @Id
    private String id;

    private String workspaceId;

    private String userId; // link to auth user

    private String fullName;

    private String registrationNumber; // medical license

    private String specialization;

    private boolean active;

    private Instant createdAt;
}
