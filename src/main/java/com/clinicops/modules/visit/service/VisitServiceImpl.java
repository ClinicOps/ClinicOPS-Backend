package com.clinicops.modules.visit.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.common.exception.NotFoundException;
import com.clinicops.modules.appointment.model.Appointment;
import com.clinicops.modules.appointment.repo.AppointmentRepository;
import com.clinicops.modules.patient.model.Patient;
import com.clinicops.modules.patient.model.PatientStatus;
import com.clinicops.modules.patient.repo.PatientRepository;
import com.clinicops.modules.visit.dto.VisitCreateRequest;
import com.clinicops.modules.visit.dto.VisitResponse;
import com.clinicops.modules.visit.model.Visit;
import com.clinicops.modules.visit.repo.VisitRepository;
import com.clinicops.security.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

	private final VisitRepository visitRepository;
	private final PatientRepository patientRepository;
	private final AppointmentRepository appointmentRepository;

	@Override
	public VisitResponse createVisit(AuthUser user, VisitCreateRequest request) {

		Patient patient = patientRepository.findById(request.getPatientId())
				.orElseThrow(() -> new NotFoundException("Patient not found"));

		if (!patient.getWorkspaceId().equals(user.getWorkspaceId())) {
			throw new BusinessException("Cross-workspace access denied");
		}

		if (patient.getStatus() == PatientStatus.MERGED) {
			throw new BusinessException("Cannot create visit for merged patient");
		}

		// Optional appointment validation
		if (request.getAppointmentId() != null) {
			Appointment appt = appointmentRepository.findById(request.getAppointmentId())
					.orElseThrow(() -> new BusinessException("Appointment not found"));

			if (!appt.getWorkspaceId().equals(user.getWorkspaceId())) {
				throw new BusinessException("Cross-workspace access denied");
			}
		}

		Visit visit = new Visit();
		visit.setWorkspaceId(user.getWorkspaceId());
		visit.setPatientId(patient.getId());
		visit.setDoctorId(request.getDoctorId());
		visit.setAppointmentId(request.getAppointmentId());
		visit.setVisitTime(Instant.now());
		visit.setReason(request.getReason());
		visit.setCreatedAt(Instant.now());

		visitRepository.save(visit);

		return toResponse(visit);
	}

	@Override
	public List<VisitResponse> getVisitHistory(AuthUser user, String patientId) {

		Patient patient = patientRepository.findById(patientId)
				.orElseThrow(() -> new NotFoundException("Patient not found"));

		if (!patient.getWorkspaceId().equals(user.getWorkspaceId())) {
			throw new BusinessException("Cross-workspace access denied");
		}

		// If merged → redirect to target patient
		String effectivePatientId = patient.getStatus() == PatientStatus.MERGED ? patient.getMergedIntoPatientId()
				: patient.getId();

		return visitRepository
				.findByWorkspaceIdAndPatientIdOrderByVisitTimeDesc(user.getWorkspaceId(), effectivePatientId).stream()
				.map(this::toResponse).toList();
	}

	private VisitResponse toResponse(Visit v) {
		return new VisitResponse(v.getId(), v.getPatientId(), v.getDoctorId(), v.getVisitTime(), v.getReason());
	}
}
