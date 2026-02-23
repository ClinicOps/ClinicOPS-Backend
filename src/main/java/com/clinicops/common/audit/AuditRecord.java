package com.clinicops.common.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditRecord {

    private String userId;
    private String clinicId;

    private String domain;
    private String resource;
    private String action;

    private Instant timestamp;

    private Map<String, Object> metadata;
}