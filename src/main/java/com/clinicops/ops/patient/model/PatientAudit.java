package com.clinicops.ops.patient.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("patient_audit")
public class PatientAudit {

    @Id
    private ObjectId id;

    private ObjectId patientId;
    private ObjectId clinicId;

    private String action;
    private ObjectId performedBy;

    private Instant timestamp;
    private String summary;

    protected PatientAudit() {}

    public PatientAudit(ObjectId patientId,
                        ObjectId clinicId,
                        String action,
                        ObjectId performedBy,
                        String summary) {

        this.patientId = patientId;
        this.clinicId = clinicId;
        this.action = action;
        this.performedBy = performedBy;
        this.summary = summary;
        this.timestamp = Instant.now();
    }
}
