package com.clinicops.modules.billing.dto;

import java.util.List;

import com.clinicops.modules.billing.model.InvoiceLineItem;

import lombok.Data;

@Data
public class InvoiceCreateRequest {

    private String visitId;

    private List<InvoiceLineItem> items;
}
