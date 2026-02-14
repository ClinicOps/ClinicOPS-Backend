package com.clinicops.web.ops;

import com.clinicops.domain.ops.service.PatientService;
import com.clinicops.web.ops.dto.CreatePatientRequest;
import com.clinicops.web.ops.dto.PatientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clinics/{clinicId}/patients")
@RequiredArgsConstructor
public class PatientController {

	private final PatientService patientService;

	@PostMapping
	public PatientResponse create(@PathVariable String clinicId, @RequestBody CreatePatientRequest request) {
		return patientService.create(clinicId, request);
	}

	@GetMapping
	public Page<PatientResponse> list(@PathVariable String clinicId, Pageable pageable) {
		return patientService.list(clinicId, pageable);
	}
	
	@GetMapping("/{patientId}")
	public PatientResponse getById(@PathVariable String clinicId,
	                               @PathVariable String patientId) {
	    return patientService.getById(clinicId, patientId);
	}


	@PutMapping("/{patientId}")
	public PatientResponse update(@PathVariable String clinicId, @PathVariable String patientId,
			@RequestBody CreatePatientRequest request) {
		return patientService.update(clinicId, patientId, request);
	}

	@PatchMapping("/{patientId}/archive")
	public void archive(@PathVariable String clinicId, @PathVariable String patientId) {
		patientService.archive(clinicId, patientId);
	}

	@PatchMapping("/{patientId}/activate")
	public void activate(@PathVariable String clinicId, @PathVariable String patientId) {
		patientService.activate(clinicId, patientId);
	}

}
