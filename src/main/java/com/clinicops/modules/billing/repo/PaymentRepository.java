package com.clinicops.modules.billing.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.billing.model.Payment;

public interface PaymentRepository extends MongoRepository<Payment, String> {

	List<Payment> findByInvoiceId(String invoiceId);
}
