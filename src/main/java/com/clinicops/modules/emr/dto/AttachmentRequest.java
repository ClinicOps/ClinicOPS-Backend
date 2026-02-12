package com.clinicops.modules.emr.dto;

import lombok.Data;

@Data
public class AttachmentRequest {
    private String fileId;
    private String fileName;
    private String contentType;
    private long size;
}
