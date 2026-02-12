package com.clinicops.modules.billing.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "payments")
@Data
@CompoundIndexes({ @CompoundIndex(name = "invoice_idx", def = "{'invoiceId':1}") })
public class Payment {

	@Id
	private String id;

	private String workspaceId;

	private String invoiceId;

	private BigDecimal amount;

	private PaymentMethod method;

	private String reference; // UPI txn / receipt no

	private Instant paidAt;
}
