package com.clinicops.modules.patient.api;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.modules.patient.dto.PatientCreateRequest;
import com.clinicops.modules.patient.dto.PatientMergeRequest;
import com.clinicops.modules.patient.dto.PatientResponse;
import com.clinicops.modules.patient.service.PatientService;
import com.clinicops.security.annotation.RequirePermission;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/patients")
@RequiredArgsConstructor
public class PatientController {

	private final PatientService service;

	@PostMapping
	@RequirePermission(Permission.PATIENT_CREATE)
	public ApiResponse<PatientResponse> create(@RequestBody PatientCreateRequest request) {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return ApiResponse.ok(service.createPatient(user, request));
	}

	@PostMapping("/merge")
	@RequirePermission(Permission.PATIENT_CREATE)
	public ApiResponse<?> merge(@RequestBody PatientMergeRequest request) {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		service.mergePatients(user, request);
		return ApiResponse.ok(null);
	}

	@GetMapping
	@RequirePermission(Permission.PATIENT_READ)
	public ApiResponse<List<PatientResponse>> list() {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return ApiResponse.ok(service.listPatients(user));
	}
	
	@GetMapping("/search")
	@RequirePermission(Permission.PATIENT_READ)
	public ApiResponse<List<PatientResponse>> search(@RequestParam String q) {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return ApiResponse.ok(service.searchPatients(user, q));
	}
}
