package com.clinicops.modules.patient.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.patient.model.Patient;
import com.clinicops.modules.patient.model.PatientStatus;

public interface PatientRepository extends MongoRepository<Patient, String> {

	Optional<Patient> findByWorkspaceIdAndPhoneAndStatus(String workspaceId, String phone, PatientStatus status);

	List<Patient> findByWorkspaceIdAndStatus(String workspaceId, PatientStatus status);

	// 🔍 SEARCH
	List<Patient> findTop20ByWorkspaceIdAndStatusAndNormalizedNameStartingWith(String workspaceId, PatientStatus status,
			String normalizedName);

	List<Patient> findTop20ByWorkspaceIdAndStatusAndPhoneLastDigitsEndingWith(String workspaceId, PatientStatus status,
			String phoneLastDigits);
}
