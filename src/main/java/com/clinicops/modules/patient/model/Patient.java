package com.clinicops.modules.patient.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "patients")
@Data
		@CompoundIndexes({ @CompoundIndex(name = "workspace_phone_idx", def = "{'workspaceId':1, 'phone':1}"),
		@CompoundIndex(name = "workspace_name_idx", def = "{'workspaceId':1, 'normalizedName':1}"),
		@CompoundIndex(name = "workspace_phone_last_idx", def = "{'workspaceId':1, 'phoneLastDigits':1}") })

public class Patient {

    @Id
    private String id;

    private String workspaceId;

    private String fullName;

    private String phone;

    private String email;

    private Integer age;

    private String gender;

    private PatientStatus status;

    private String mergedIntoPatientId; // if MERGED

    private Instant createdAt;
    
    private String normalizedName;   // lowercased, trimmed
    
    private String phoneLastDigits;  // last 6 digits
}

