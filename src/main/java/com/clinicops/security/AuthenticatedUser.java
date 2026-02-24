package com.clinicops.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {

    private ObjectId userId;
    private ObjectId organizationId;
    private ObjectId clinicId;
    private String role;
}