package com.clinicops.ops.patient.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PatientPersonal {

    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private String photoUrl;

    protected PatientPersonal() {}

    public PatientPersonal(String firstName,
                           String lastName,
                           Gender gender,
                           LocalDate dateOfBirth,
                           String bloodGroup,
                           String photoUrl) {

        if (firstName == null || firstName.trim().length() < 2) {
            throw new IllegalArgumentException("Invalid first name");
        }

        this.firstName = firstName.trim();
        this.lastName = lastName != null ? lastName.trim() : null;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.bloodGroup = bloodGroup;
        this.photoUrl = photoUrl;
    }
}
