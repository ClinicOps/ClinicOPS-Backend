package com.clinicops.modules.emr.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.emr.model.EmrRecord;

public interface EmrRecordRepository extends MongoRepository<EmrRecord, String> {

	List<EmrRecord> findByVisitIdOrderByVersionDesc(String visitId);
}
