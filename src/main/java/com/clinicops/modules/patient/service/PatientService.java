package com.clinicops.modules.patient.service;

import java.util.List;

import com.clinicops.modules.patient.dto.PatientCreateRequest;
import com.clinicops.modules.patient.dto.PatientMergeRequest;
import com.clinicops.modules.patient.dto.PatientResponse;
import com.clinicops.security.model.AuthUser;

public interface PatientService {

	PatientResponse createPatient(AuthUser user, PatientCreateRequest request);

	void mergePatients(AuthUser user, PatientMergeRequest request);

	List<PatientResponse> listPatients(AuthUser user);

	List<PatientResponse> searchPatients(AuthUser user, String query);
}
