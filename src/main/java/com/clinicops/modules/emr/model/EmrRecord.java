package com.clinicops.modules.emr.model;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "emr_records")
@Data
@CompoundIndexes({
    @CompoundIndex(
        name = "visit_version_idx",
        def = "{'visitId':1, 'version':-1}"
    )
})
public class EmrRecord {

    @Id
    private String id;

    private String workspaceId;

    private String visitId;

    private int version; // 1, 2, 3...

    private List<Diagnosis> diagnoses;

    private List<Prescription> prescriptions;

    private String clinicalNotes;

    private List<EmrAttachment> attachments;

    private String createdByDoctorId;

    private Instant createdAt;
}
