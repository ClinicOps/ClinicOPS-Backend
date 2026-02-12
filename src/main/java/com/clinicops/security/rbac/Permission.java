package com.clinicops.security.rbac;

public enum Permission {
    PATIENT_READ,
    PATIENT_CREATE,
    
    CLINIC_MANAGE,
    
    DOCTOR_MANAGE,
    DOCTOR_READ,
    
    DEPARTMENT_MANAGE,
    DEPARTMENT_READ,
    
    APPOINTMENT_READ,
    APPOINTMENT_BOOK,
    
    EMR_READ,
    EMR_WRITE,
    
    BILLING_READ,
    BILLING_WRITE,
    
    ADMIN_READ,
    OPS_EXECUTE,
    ANALYTICS_READ,
    
    WORKSPACES_READ

    
}
