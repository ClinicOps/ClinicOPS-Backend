package com.clinicops.modules.billing.dto;

import java.math.BigDecimal;

import com.clinicops.modules.billing.model.PaymentMethod;

import lombok.Data;

@Data
public class PaymentCreateRequest {

    private String invoiceId;
    private BigDecimal amount;
    private PaymentMethod method;
    private String reference;
}

