package com.clinicops.modules.workspace.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "workspaces")
@Data
public class Workspace {
    @Id
    private String id;
    private String name;
    private WorkspaceStatus status;
    private Instant createdAt;
}
