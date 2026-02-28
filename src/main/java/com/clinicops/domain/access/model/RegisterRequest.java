package com.clinicops.domain.access.model;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    
    // Clinic setup fields
    private String clinicName;
    private String clinicCode;
    private String clinicTimezone;
    
    // Organization (optional - will auto-create if not provided)
    private String organizationName;
}
