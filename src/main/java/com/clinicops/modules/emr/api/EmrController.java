package com.clinicops.modules.emr.api;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.modules.emr.dto.EmrCreateRequest;
import com.clinicops.modules.emr.dto.EmrResponse;
import com.clinicops.modules.emr.service.EmrService;
import com.clinicops.security.annotation.RequirePermission;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/emr")
@RequiredArgsConstructor
public class EmrController {

	private final EmrService service;

	@PostMapping
	@RequirePermission(Permission.EMR_WRITE)
	public ApiResponse<EmrResponse> create(@RequestBody EmrCreateRequest request) {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return ApiResponse.ok(service.createEmr(user, request));
	}

	@GetMapping("/visit/{visitId}")
	@RequirePermission(Permission.EMR_READ)
	public ApiResponse<EmrResponse> latest(@PathVariable String visitId) {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return ApiResponse.ok(service.getLatestEmr(user, visitId));
	}

	@GetMapping("/visit/{visitId}/history")
	@RequirePermission(Permission.EMR_READ)
	public ApiResponse<List<EmrResponse>> history(@PathVariable String visitId) {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return ApiResponse.ok(service.getEmrHistory(user, visitId));
	}
}
