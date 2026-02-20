package com.clinicops.ops.patient.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePatientRequest {

    // personal
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private String photoUrl;

    // contact
    private String mobile;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pincode;

    // medical
    private List<String> allergies;
    private List<String> chronicConditions;
    private String notes;
}
	