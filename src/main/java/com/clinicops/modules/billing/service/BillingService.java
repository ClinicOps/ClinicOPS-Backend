package com.clinicops.modules.billing.service;

import com.clinicops.modules.billing.dto.InvoiceCreateRequest;
import com.clinicops.modules.billing.dto.InvoiceResponse;
import com.clinicops.modules.billing.dto.PaymentCreateRequest;
import com.clinicops.security.model.AuthUser;

public interface BillingService {

	InvoiceResponse createInvoice(AuthUser user, InvoiceCreateRequest request);

	InvoiceResponse recordPayment(AuthUser user, PaymentCreateRequest request);

	InvoiceResponse getInvoiceByVisit(AuthUser user, String visitId);
}
