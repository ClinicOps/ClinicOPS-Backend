package com.clinicops.security;

import java.util.Objects;

public class AuthenticatedUser {

    private final String userId;

    public AuthenticatedUser(String userId) {
        this.userId = Objects.requireNonNull(userId);
    }

    public String getUserId() {
        return userId;
    }
}
