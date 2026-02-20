package com.clinicops.ops.patient.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PatientResponse {

    String id;
    String patientCode;

    String firstName;
    String lastName;

    String mobile;
    String email;
    
    String gender;

    String status;
}
