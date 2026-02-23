package com.clinicops.ops.availability.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.ops.availability.model.DoctorAvailability;

public interface DoctorAvailabilityRepository
extends MongoRepository<DoctorAvailability, ObjectId> {

List<DoctorAvailability> findByClinicIdAndDoctorIdAndDayOfWeekAndIsActiveTrue(
ObjectId clinicId,
ObjectId doctorId,
DayOfWeek dayOfWeek
);
}
