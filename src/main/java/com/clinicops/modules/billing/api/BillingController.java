package com.clinicops.modules.billing.api;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicops.common.api.ApiResponse;
import com.clinicops.modules.billing.dto.InvoiceCreateRequest;
import com.clinicops.modules.billing.dto.InvoiceResponse;
import com.clinicops.modules.billing.dto.PaymentCreateRequest;
import com.clinicops.modules.billing.service.BillingService;
import com.clinicops.security.annotation.RequirePermission;
import com.clinicops.security.model.AuthUser;
import com.clinicops.security.rbac.Permission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/billing")
@RequiredArgsConstructor
public class BillingController {

	private final BillingService service;

	@PostMapping("/invoice")
	@RequirePermission(Permission.BILLING_WRITE)
	public ApiResponse<InvoiceResponse> createInvoice(@RequestBody InvoiceCreateRequest request) {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return ApiResponse.ok(service.createInvoice(user, request));
	}

	@PostMapping("/payment")
	@RequirePermission(Permission.BILLING_WRITE)
	public ApiResponse<InvoiceResponse> pay(@RequestBody PaymentCreateRequest request) {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return ApiResponse.ok(service.recordPayment(user, request));
	}

	@GetMapping("/visit/{visitId}")
	@RequirePermission(Permission.BILLING_READ)
	public ApiResponse<InvoiceResponse> getByVisit(@PathVariable String visitId) {

		AuthUser user = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return ApiResponse.ok(service.getInvoiceByVisit(user, visitId));
	}
}
