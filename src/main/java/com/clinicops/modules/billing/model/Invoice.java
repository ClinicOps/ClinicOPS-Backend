package com.clinicops.modules.billing.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "invoices")
@Data
@CompoundIndexes({ @CompoundIndex(name = "visit_idx", def = "{'visitId':1}") })
public class Invoice {

	@Id
	private String id;

	private String workspaceId;

	private String visitId;

	private List<InvoiceLineItem> items;

	private BigDecimal totalAmount;

	private InvoiceStatus status;

	private Instant createdAt;
}
