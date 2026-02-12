package com.clinicops.modules.visit.api;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.modules.visit.dto.VisitCreateRequest;
import com.clinicops.modules.visit.dto.VisitResponse;
import com.clinicops.modules.visit.service.VisitService;
import com.clinicops.security.annotation.RequirePermission;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/visits")
@RequiredArgsConstructor
public class VisitController {

	private final VisitService service;

	@PostMapping
	@RequirePermission(Permission.PATIENT_READ)
	public ApiResponse<VisitResponse> create(@RequestBody VisitCreateRequest request) {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return ApiResponse.ok(service.createVisit(user, request));
	}

	@GetMapping("/patient/{patientId}")
	@RequirePermission(Permission.PATIENT_READ)
	public ApiResponse<List<VisitResponse>> history(@PathVariable String patientId) {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return ApiResponse.ok(service.getVisitHistory(user, patientId));
	}
}
