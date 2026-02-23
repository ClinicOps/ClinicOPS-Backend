package com.clinicops.domain.clinic.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClinicRequest {
	
	private String organizationName;
	private String clinicName;
	private String timezone;

}
