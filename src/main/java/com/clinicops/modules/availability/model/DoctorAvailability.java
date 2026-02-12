package com.clinicops.modules.availability.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "doctor_availability")
@Data
public class DoctorAvailability {

    @Id
    private String id;

    private String workspaceId;

    private String doctorId;

    private WeeklyAvailability weekly;

    private List<AvailabilityOverride> overrides = new ArrayList<>();

    private Instant updatedAt;
}
