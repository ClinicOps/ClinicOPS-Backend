package com.clinicops.modules.billing.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InvoiceLineItem {

    private String description; // Consultation, X-Ray, etc
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
}
