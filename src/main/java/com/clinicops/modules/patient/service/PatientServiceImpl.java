package com.clinicops.modules.patient.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.common.exception.NotFoundException;
import com.clinicops.modules.patient.dto.PatientCreateRequest;
import com.clinicops.modules.patient.dto.PatientMergeRequest;
import com.clinicops.modules.patient.dto.PatientResponse;
import com.clinicops.modules.patient.model.Patient;
import com.clinicops.modules.patient.model.PatientStatus;
import com.clinicops.modules.patient.repo.PatientRepository;
import com.clinicops.security.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

	private final PatientRepository repository;

	@Override
	public PatientResponse createPatient(AuthUser user, PatientCreateRequest request) {

		repository.findByWorkspaceIdAndPhoneAndStatus(user.getWorkspaceId(), request.getPhone(), PatientStatus.ACTIVE)
				.ifPresent(p -> {
					throw new BusinessException("Patient already exists");
				});

		Patient p = new Patient();
		p.setWorkspaceId(user.getWorkspaceId());
		p.setFullName(request.getFullName());
		p.setPhone(request.getPhone());
		p.setEmail(request.getEmail());
		p.setAge(request.getAge());
		p.setGender(request.getGender());
		p.setStatus(PatientStatus.ACTIVE);
		p.setCreatedAt(Instant.now());
		p.setNormalizedName(
			    request.getFullName().toLowerCase().trim()
			);
		String phone = request.getPhone();
		p.setPhoneLastDigits(
		    phone.length() > 6
		        ? phone.substring(phone.length() - 6)
		        : phone
		);

		repository.save(p);
		return toResponse(p);
	}

	@Override
	public void mergePatients(AuthUser user, PatientMergeRequest request) {

		Patient source = repository.findById(request.getSourcePatientId())
				.orElseThrow(() -> new NotFoundException("Source patient not found"));

		Patient target = repository.findById(request.getTargetPatientId())
				.orElseThrow(() -> new NotFoundException("Target patient not found"));

		if (!source.getWorkspaceId().equals(user.getWorkspaceId())
				|| !target.getWorkspaceId().equals(user.getWorkspaceId())) {
			throw new BusinessException("Cross-workspace access denied");
		}

		if (source.getStatus() != PatientStatus.ACTIVE || target.getStatus() != PatientStatus.ACTIVE) {
			throw new BusinessException("Invalid merge state");
		}

		source.setStatus(PatientStatus.MERGED);
		source.setMergedIntoPatientId(target.getId());

		repository.save(source);
	}

	@Override
	public List<PatientResponse> listPatients(AuthUser user) {

		return repository.findByWorkspaceIdAndStatus(user.getWorkspaceId(), PatientStatus.ACTIVE).stream()
				.map(this::toResponse).toList();
	}

	private PatientResponse toResponse(Patient p) {
		return new PatientResponse(p.getId(), p.getFullName(), p.getPhone(), p.getAge(), p.getGender());
	}
	
	@Override
	public List<PatientResponse> searchPatients(AuthUser user, String query) {

		if (query == null || query.trim().length() < 2) {
			return List.of();
		}

		String trimmed = query.trim();

		List<Patient> results;

		// Digits → phone search
		if (trimmed.matches("\\d+")) {

			String lastDigits = trimmed.length() > 6 ? trimmed.substring(trimmed.length() - 6) : trimmed;

			results = repository.findTop20ByWorkspaceIdAndStatusAndPhoneLastDigitsEndingWith(user.getWorkspaceId(),
					PatientStatus.ACTIVE, lastDigits);

		} else {
			// Text → name search
			results = repository.findTop20ByWorkspaceIdAndStatusAndNormalizedNameStartingWith(user.getWorkspaceId(),
					PatientStatus.ACTIVE, trimmed.toLowerCase());
		}

		return results.stream().map(this::toResponse).toList();
	}

}
