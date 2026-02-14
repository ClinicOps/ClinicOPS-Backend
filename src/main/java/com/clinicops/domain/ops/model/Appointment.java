package com.clinicops.domain.ops.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

import lombok.Data;

import java.time.Instant;

@Document("appointments")
@Data
public class Appointment{

    @Id
    private ObjectId id;

    private ObjectId clinicId;
    private String patientName;
    private Instant scheduledAt;
    private String status; // CREATED, CONFIRMED, CANCELLED

    protected Appointment() {}

    public Appointment(ObjectId clinicId, String patientName, Instant scheduledAt) {
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

    public ObjectId getClinicId() {
        return clinicId;
    }

    public ObjectId getId() {
        return id;
    }
}
