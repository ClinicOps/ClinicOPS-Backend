package com.clinicops.domain.ops.service;

import com.clinicops.web.ops.dto.CreatePatientRequest;
import com.clinicops.web.ops.dto.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {

    PatientResponse create(String clinicId, CreatePatientRequest request);

    Page<PatientResponse> list(String clinicId, Pageable pageable);
}
