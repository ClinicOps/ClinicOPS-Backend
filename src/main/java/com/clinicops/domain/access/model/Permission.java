package com.clinicops.domain.access.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document("permissions")
@Data
public class Permission {

    @Id
    private String id;

    private String domain;
    private String resource;
    private String action;

    public Permission(String domain, String resource, String action) {
        this.domain = domain;
        this.resource = resource;
        this.action = action;
    }

    public String key() {
        return domain + ":" + resource + ":" + action;
    }
}
