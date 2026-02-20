package com.clinicops.ops.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.clinicops.ops.patient.dto.CreatePatientRequest;
import com.clinicops.ops.patient.dto.PatientResponse;

public interface PatientService {

    PatientResponse create(String clinicId, CreatePatientRequest request);

    Page<PatientResponse> list(String clinicId, int page, int size, String query, String status);
    
    PatientResponse getById(String clinicId, String patientId);
    
    PatientResponse update(String clinicId, String patientId, CreatePatientRequest request);

    void archive(String clinicId, String patientId);

    void activate(String clinicId, String patientId);


}
