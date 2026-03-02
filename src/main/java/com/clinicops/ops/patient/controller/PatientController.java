package com.clinicops.ops.patient.controller;

import com.clinicops.ops.patient.dto.CreatePatientRequest;
import com.clinicops.ops.patient.dto.PatientResponse;
import com.clinicops.ops.patient.service.PatientService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ops/patients")
@RequiredArgsConstructor
public class PatientController {

	private final PatientService patientService;

	@PostMapping
	public PatientResponse create(@RequestBody CreatePatientRequest request, HttpServletRequest httpRequest) {
		ObjectId clinicId = new ObjectId((String) httpRequest.getAttribute("CLINIC_ID"));
		return patientService.create(clinicId.toString(), request);
	}

	@GetMapping
	public Page<PatientResponse> list(
			@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String query,
	        @RequestParam(required = false) String status, HttpServletRequest httpRequest) {
		ObjectId clinicId = new ObjectId((String) httpRequest.getAttribute("CLINIC_ID"));
		return patientService.list(clinicId.toString(), page, size, query, status);
	}
	
	@GetMapping("/{patientId}")
	public PatientResponse getById(@PathVariable String patientId, HttpServletRequest httpRequest) {
		ObjectId clinicId = new ObjectId((String) httpRequest.getAttribute("CLINIC_ID"));
	    return patientService.getById(clinicId.toString(), patientId);
	}

	@PutMapping("/{patientId}")
	public PatientResponse update(@PathVariable String patientId,
			@RequestBody CreatePatientRequest request, HttpServletRequest httpRequest) {
		ObjectId clinicId = new ObjectId((String) httpRequest.getAttribute("CLINIC_ID"));
		return patientService.update(clinicId.toString(), patientId, request);
	}

	@PatchMapping("/{patientId}/archive")
	public void archive(@PathVariable String patientId, HttpServletRequest httpRequest) {
		ObjectId clinicId = new ObjectId((String) httpRequest.getAttribute("CLINIC_ID"));
		patientService.archive(clinicId.toString(), patientId);
	}

	@PatchMapping("/{patientId}/activate")
	public void activate(@PathVariable String patientId, HttpServletRequest httpRequest) {
		ObjectId clinicId = new ObjectId((String) httpRequest.getAttribute("CLINIC_ID"));
		patientService.activate(clinicId.toString(), patientId);
	}

}
