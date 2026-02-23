package com.clinicops.domain.clinic.service;

import org.springframework.stereotype.Service;

import com.clinicops.domain.clinic.dto.CreateClinicRequest;
import com.clinicops.domain.clinic.model.Clinic;

@Service
public interface ClinicService {
	
	Clinic createInitialClinic(CreateClinicRequest request);

}
