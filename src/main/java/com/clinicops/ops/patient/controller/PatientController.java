package com.clinicops.ops.patient.controller;

import com.clinicops.ops.patient.dto.CreatePatientRequest;
import com.clinicops.ops.patient.dto.PatientResponse;
import com.clinicops.ops.patient.service.PatientService;
import com.clinicops.security.SecurityUtils;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ops/patients")
@RequiredArgsConstructor
public class PatientController {

	private final PatientService patientService;

	@PostMapping
	public PatientResponse create(@RequestBody CreatePatientRequest request) {
		ObjectId clinicId = SecurityUtils.getCurrentClinicId();
		return patientService.create(clinicId.toString(), request);
	}

	@GetMapping
	public Page<PatientResponse> list(
			@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String query,
	        @RequestParam(required = false) String status) {
		ObjectId clinicId = SecurityUtils.getCurrentClinicId();
		return patientService.list(clinicId.toString(), page, size, query, status);
	}
	
	@GetMapping("/{patientId}")
	public PatientResponse getById(@PathVariable String patientId) {
		ObjectId clinicId = SecurityUtils.getCurrentClinicId();
	    return patientService.getById(clinicId.toString(), patientId);
	}

	@PutMapping("/{patientId}")
	public PatientResponse update(@PathVariable String patientId,
			@RequestBody CreatePatientRequest request) {
		ObjectId clinicId = SecurityUtils.getCurrentClinicId();
		return patientService.update(clinicId.toString(), patientId, request);
	}

	@PatchMapping("/{patientId}/archive")
	public void archive(@PathVariable String patientId) {
		ObjectId clinicId = SecurityUtils.getCurrentClinicId();
		patientService.archive(clinicId.toString(), patientId);
	}

	@PatchMapping("/{patientId}/activate")
	public void activate(@PathVariable String patientId) {
		ObjectId clinicId = SecurityUtils.getCurrentClinicId();
		patientService.activate(clinicId.toString(), patientId);
	}

}
