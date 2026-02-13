package com.clinicops.domain.ops.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.time.Instant;

@Document("appointments")
@Data
public class Appointment {

    @Id
    private String id;

    private String clinicId;
    private String patientName;
    private Instant scheduledAt;
    private String status; // CREATED, CONFIRMED, CANCELLED

    protected Appointment() {}

    public Appointment(String clinicId, String patientName, Instant scheduledAt) {
        this.clinicId = clinicId;
        this.patientName = patientName;
        this.scheduledAt = scheduledAt;
        this.status = "CREATED";
    }

    public void reschedule(Instant newTime) {
        this.scheduledAt = newTime;
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    public String getClinicId() {
        return clinicId;
    }

    public String getId() {
        return id;
    }
}
