package com.clinicops.domain.clinic.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private String pincode;
}
