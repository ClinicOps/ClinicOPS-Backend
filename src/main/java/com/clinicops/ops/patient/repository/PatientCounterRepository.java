package com.clinicops.ops.patient.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.ops.patient.counter.PatientCounter;

public interface PatientCounterRepository extends MongoRepository<PatientCounter, ObjectId> {
}
