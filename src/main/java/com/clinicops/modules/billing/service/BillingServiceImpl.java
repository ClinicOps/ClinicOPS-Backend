package com.clinicops.modules.billing.service;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.common.exception.NotFoundException;
import com.clinicops.modules.billing.dto.InvoiceCreateRequest;
import com.clinicops.modules.billing.dto.InvoiceResponse;
import com.clinicops.modules.billing.dto.PaymentCreateRequest;
import com.clinicops.modules.billing.model.Invoice;
import com.clinicops.modules.billing.model.InvoiceLineItem;
import com.clinicops.modules.billing.model.InvoiceStatus;
import com.clinicops.modules.billing.model.Payment;
import com.clinicops.modules.billing.repo.InvoiceRepository;
import com.clinicops.modules.billing.repo.PaymentRepository;
import com.clinicops.modules.visit.model.Visit;
import com.clinicops.modules.visit.repo.VisitRepository;
import com.clinicops.security.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

	private final InvoiceRepository invoiceRepo;
	private final PaymentRepository paymentRepo;
	private final VisitRepository visitRepo;

	@Override
	public InvoiceResponse createInvoice(AuthUser user, InvoiceCreateRequest request) {

		Visit visit = visitRepo.findById(request.getVisitId())
				.orElseThrow(() -> new NotFoundException("Visit not found"));

		if (!visit.getWorkspaceId().equals(user.getWorkspaceId())) {
			throw new BusinessException("Cross-workspace access denied");
		}

		if (invoiceRepo.findByVisitId(visit.getId()).isPresent()) {
			throw new BusinessException("Invoice already exists");
		}

		BigDecimal total = request.getItems().stream().map(InvoiceLineItem::getTotal).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		Invoice invoice = new Invoice();
		invoice.setWorkspaceId(user.getWorkspaceId());
		invoice.setVisitId(visit.getId());
		invoice.setItems(request.getItems());
		invoice.setTotalAmount(total);
		invoice.setStatus(InvoiceStatus.FINALIZED);
		invoice.setCreatedAt(Instant.now());

		invoiceRepo.save(invoice);
		return toResponse(invoice);
	}

	@Override
	public InvoiceResponse recordPayment(AuthUser user, PaymentCreateRequest request) {

		Invoice invoice = invoiceRepo.findById(request.getInvoiceId())
				.orElseThrow(() -> new NotFoundException("Invoice not found"));

		if (!invoice.getWorkspaceId().equals(user.getWorkspaceId())) {
			throw new BusinessException("Cross-workspace access denied");
		}

		Payment payment = new Payment();
		payment.setWorkspaceId(user.getWorkspaceId());
		payment.setInvoiceId(invoice.getId());
		payment.setAmount(request.getAmount());
		payment.setMethod(request.getMethod());
		payment.setReference(request.getReference());
		payment.setPaidAt(Instant.now());

		paymentRepo.save(payment);

		BigDecimal paidSoFar = paymentRepo.findByInvoiceId(invoice.getId()).stream().map(Payment::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		if (paidSoFar.compareTo(invoice.getTotalAmount()) >= 0) {
			invoice.setStatus(InvoiceStatus.PAID);
		} else {
			invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
		}

		invoiceRepo.save(invoice);
		return toResponse(invoice);
	}

	@Override
	public InvoiceResponse getInvoiceByVisit(AuthUser user, String visitId) {

		Invoice invoice = invoiceRepo.findByVisitId(visitId)
				.orElseThrow(() -> new NotFoundException("Invoice not found"));

		return toResponse(invoice);
	}

	private InvoiceResponse toResponse(Invoice invoice) {
		return new InvoiceResponse(invoice.getId(), invoice.getVisitId(), invoice.getItems(), invoice.getTotalAmount(),
				invoice.getStatus());
	}
}
