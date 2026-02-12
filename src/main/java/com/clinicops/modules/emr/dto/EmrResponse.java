package com.clinicops.modules.emr.dto;

import java.time.Instant;
import java.util.List;

import com.clinicops.modules.emr.model.Diagnosis;
import com.clinicops.modules.emr.model.EmrAttachment;
import com.clinicops.modules.emr.model.Prescription;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmrResponse {

    private String id;
    private String visitId;
    private int version;
    private List<Diagnosis> diagnoses;
    private List<Prescription> prescriptions;
    private String clinicalNotes;
    private List<EmrAttachment> attachments;
    private Instant createdAt;
}
