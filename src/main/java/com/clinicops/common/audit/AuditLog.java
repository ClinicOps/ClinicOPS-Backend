package com.clinicops.common.audit;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("audit_logs")
@CompoundIndex(
        name = "clinic_time_idx",
        def = "{'clinicId':1, 'timestamp':-1}"
)
public class AuditLog {

    @Id
    private ObjectId id;

    private ObjectId userId;
    private String clinicId;

    private String domain;
    private String resource;
    private String action;

    private Instant timestamp;

    private Map<String, Object> metadata;
}