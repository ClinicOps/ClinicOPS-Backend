package com.clinicops.domain.ops.repository;

import com.clinicops.domain.ops.model.PatientCounter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientCounterRepository extends MongoRepository<PatientCounter, ObjectId> {
}
