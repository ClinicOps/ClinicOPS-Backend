package com.clinicops.domain.clinic.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.domain.clinic.dto.CreateClinicRequest;
import com.clinicops.domain.clinic.repository.ClinicMemberRepository;
import com.clinicops.domain.clinic.service.ClinicService;
import com.clinicops.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {
	
	@Autowired
	ClinicService clinicService;
	@Autowired
	ClinicMemberRepository clinicMemberRepository;
	
	@PostMapping("/setup")
	public ResponseEntity<?> createInitialClinic(
	        @RequestBody CreateClinicRequest request) {

		clinicService.createInitialClinic(request);
	    return ResponseEntity.ok().build();
	}
	
	@GetMapping("/my-membership")
	public ResponseEntity<?> hasMembership() {

	    ObjectId userId = SecurityUtils.getCurrentUserId();

	    boolean exists = clinicMemberRepository
	            .existsByUserIdAndDeletedFalse(userId);

	    return ResponseEntity.ok(exists);
	}

}
