package com.clinicops.domain.access.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document("permissions")
@Data
public class Permission {

    @Id
    private String id;

    private String domain; // CLINIC | OPS | ADMIN | ACCESS | IDENTITY
    private String resource; // USER | ROLE | APPOINTMENT | CLINIC | ...
    private String action; // VIEW | CREATE | UPDATE | DELETE | ASSIGN | OVERRIDE

    public Permission(String domain, String resource, String action) {
        this.domain = domain;
        this.resource = resource;
        this.action = action;
    }

    public String key() {
        return domain + ":" + resource + ":" + action;
    }
}
