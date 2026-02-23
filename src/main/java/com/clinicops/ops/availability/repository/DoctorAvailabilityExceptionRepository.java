package com.clinicops.ops.availability.repository;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.ops.availability.model.DoctorAvailabilityException;

public interface DoctorAvailabilityExceptionRepository
extends MongoRepository<DoctorAvailabilityException, ObjectId> {

List<DoctorAvailabilityException> findByClinicIdAndDoctorIdAndDate(
ObjectId clinicId,
ObjectId doctorId,
LocalDate date
);
}