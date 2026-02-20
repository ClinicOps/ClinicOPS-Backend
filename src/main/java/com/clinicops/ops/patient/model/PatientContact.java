package com.clinicops.ops.patient.model;

import lombok.Getter;

@Getter
public class PatientContact {

    private String mobile;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pincode;

    protected PatientContact() {}

    public PatientContact(String mobile,
                          String email,
                          String address,
                          String city,
                          String state,
                          String pincode) {

        if (mobile == null || mobile.trim().length() < 8) {
            throw new IllegalArgumentException("Invalid mobile number");
        }

        this.mobile = mobile.trim();
        this.email = email;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }
}
