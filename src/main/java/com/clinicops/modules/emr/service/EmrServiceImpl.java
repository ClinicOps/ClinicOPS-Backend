package com.clinicops.modules.emr.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.common.exception.NotFoundException;
import com.clinicops.modules.emr.dto.EmrCreateRequest;
import com.clinicops.modules.emr.dto.EmrResponse;
import com.clinicops.modules.emr.model.Diagnosis;
import com.clinicops.modules.emr.model.EmrAttachment;
import com.clinicops.modules.emr.model.EmrRecord;
import com.clinicops.modules.emr.model.Medication;
import com.clinicops.modules.emr.model.Prescription;
import com.clinicops.modules.emr.repo.EmrRecordRepository;
import com.clinicops.modules.visit.model.Visit;
import com.clinicops.modules.visit.repo.VisitRepository;
import com.clinicops.security.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmrServiceImpl implements EmrService {

	private final EmrRecordRepository repository;
	private final VisitRepository visitRepository;

	@Override
	public EmrResponse createEmr(AuthUser user, EmrCreateRequest request) {

		Visit visit = visitRepository.findById(request.getVisitId())
				.orElseThrow(() -> new NotFoundException("Visit not found"));

		if (!visit.getWorkspaceId().equals(user.getWorkspaceId())) {
			throw new BusinessException("Cross-workspace access denied");
		}

		int nextVersion = repository.findByVisitIdOrderByVersionDesc(visit.getId()).stream().findFirst()
				.map(r -> r.getVersion() + 1).orElse(1);

		EmrRecord emr = new EmrRecord();
		emr.setWorkspaceId(user.getWorkspaceId());
		emr.setVisitId(visit.getId());
		emr.setVersion(nextVersion);
		emr.setDiagnoses(mapDiagnoses(request));
		emr.setPrescriptions(mapPrescriptions(request));
		emr.setClinicalNotes(request.getClinicalNotes());
		emr.setAttachments(mapAttachments(request));
		emr.setCreatedByDoctorId(user.getUserId());
		emr.setCreatedAt(Instant.now());

		repository.save(emr);
		return toResponse(emr);
	}

	@Override
	public EmrResponse getLatestEmr(AuthUser user, String visitId) {

		return repository.findByVisitIdOrderByVersionDesc(visitId).stream().findFirst().map(this::toResponse)
				.orElseThrow(() -> new NotFoundException("EMR not found"));
	}

	@Override
	public List<EmrResponse> getEmrHistory(AuthUser user, String visitId) {

		return repository.findByVisitIdOrderByVersionDesc(visitId).stream().map(this::toResponse).toList();
	}

	// mapping helpers below
	private List<Diagnosis> mapDiagnoses(EmrCreateRequest request) {

		if (request.getDiagnoses() == null) {
			return List.of();
		}

		return request.getDiagnoses().stream().map(d -> {
			Diagnosis diag = new Diagnosis();
			diag.setCode(d.getCode());
			diag.setDescription(d.getDescription());
			diag.setPrimary(d.isPrimary());
			return diag;
		}).toList();
	}

	private List<Prescription> mapPrescriptions(EmrCreateRequest request) {

		if (request.getPrescriptions() == null) {
			return List.of();
		}

		return request.getPrescriptions().stream().map(p -> {
			Medication med = new Medication();
			med.setName(p.getMedicationName());
			med.setStrength(p.getStrength());

			Prescription rx = new Prescription();
			rx.setMedication(med);
			rx.setDosage(p.getDosage());
			rx.setFrequency(p.getFrequency());
			rx.setDurationDays(p.getDurationDays());
			rx.setInstructions(p.getInstructions());

			return rx;
		}).toList();
	}

	private List<EmrAttachment> mapAttachments(EmrCreateRequest request) {

		if (request.getAttachments() == null) {
			return List.of();
		}

		return request.getAttachments().stream().map(a -> {
			EmrAttachment att = new EmrAttachment();
			att.setFileId(a.getFileId());
			att.setFileName(a.getFileName());
			att.setContentType(a.getContentType());
			att.setSize(a.getSize());
			return att;
		}).toList();
	}

	private EmrResponse toResponse(EmrRecord emr) {

		return new EmrResponse(emr.getId(), emr.getVisitId(), emr.getVersion(), emr.getDiagnoses(),
				emr.getPrescriptions(), emr.getClinicalNotes(), emr.getAttachments(), emr.getCreatedAt());
	}

}
