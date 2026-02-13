package com.clinicops.domain.access.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.Set;

@Document("roles")
@Data
public class Role {

    @Id
    private String id;
    private String name; // OWNER | ADMIN | DOCTOR | STAFF | OPS
    private Set<String> permissionIds;

    public boolean isOwner() {
        return "OWNER".equalsIgnoreCase(name);
    }
}
