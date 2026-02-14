package com.clinicops.domain.ops.model;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

import lombok.Getter;

@Getter
@Document("appointments")
@CompoundIndexes({
    @CompoundIndex(name = "clinic_scheduled_idx", def = "{'clinicId': 1, 'scheduledAt': 1}"),
    @CompoundIndex(name = "clinic_status_idx", def = "{'clinicId': 1, 'status': 1}")
})
public class Appointment extends BaseEntity {

    private ObjectId clinicId;
    private String patientName;
    private Instant scheduledAt;
    private AppointmentStatus status;

    protected Appointment() {
        // For Mongo
    }

    public Appointment(ObjectId clinicId, String patientName, Instant scheduledAt) {
        if (clinicId == null) {
            throw new IllegalArgumentException("ClinicId cannot be null");
        }
        if (patientName == null || patientName.trim().length() < 2) {
            throw new IllegalArgumentException("Invalid patient name");
        }
        if (scheduledAt == null) {
            throw new IllegalArgumentException("Scheduled time required");
        }

        this.clinicId = clinicId;
        this.patientName = patientName.trim();
        this.scheduledAt = scheduledAt;
        this.status = AppointmentStatus.CREATED;
    }

    public void confirm() {
        if (this.status == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot confirm a cancelled appointment");
        }
        this.status = AppointmentStatus.CONFIRMED;
    }

    public void reschedule(Instant newTime) {
        if (this.status == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot reschedule a cancelled appointment");
        }
        if (newTime == null) {
            throw new IllegalArgumentException("New time required");
        }
        this.scheduledAt = newTime;
    }

    public void cancel() {
        if (this.status == AppointmentStatus.CANCELLED) {
            return; // idempotent
        }
        this.status = AppointmentStatus.CANCELLED;
    }

    public boolean isActive() {
        return this.status != AppointmentStatus.CANCELLED;
    }
}
