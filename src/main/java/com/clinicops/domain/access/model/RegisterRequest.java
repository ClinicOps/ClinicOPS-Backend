package com.clinicops.domain.access.model;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
}
