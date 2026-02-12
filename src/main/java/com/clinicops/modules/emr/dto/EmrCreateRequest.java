package com.clinicops.modules.emr.dto;

import java.util.List;

import lombok.Data;

@Data
public class EmrCreateRequest {

    private String visitId;

    private List<DiagnosisRequest> diagnoses;

    private List<PrescriptionRequest> prescriptions;

    private String clinicalNotes;

    private List<AttachmentRequest> attachments;
}
