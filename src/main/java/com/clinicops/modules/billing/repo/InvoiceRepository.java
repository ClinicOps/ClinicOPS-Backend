package com.clinicops.modules.billing.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.billing.model.Invoice;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {

	Optional<Invoice> findByVisitId(String visitId);
}
