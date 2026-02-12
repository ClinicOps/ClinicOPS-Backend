package com.clinicops.modules.billing.dto;

import java.math.BigDecimal;
import java.util.List;

import com.clinicops.modules.billing.model.InvoiceLineItem;
import com.clinicops.modules.billing.model.InvoiceStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceResponse {

    private String id;
    private String visitId;
    private List<InvoiceLineItem> items;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
}

