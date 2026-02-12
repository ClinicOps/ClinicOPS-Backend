package com.clinicops.modules.emr.model;

import lombok.Data;

@Data
public class EmrAttachment {

    private String fileId;     // storage reference
    private String fileName;
    private String contentType;
    private long size;
}
