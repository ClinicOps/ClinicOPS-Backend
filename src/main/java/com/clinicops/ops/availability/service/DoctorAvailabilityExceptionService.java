package com.clinicops.ops.availability.service;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.clinicops.ops.availability.model.CreateAvailabilityExceptionRequest;
import com.clinicops.ops.availability.model.DoctorAvailabilityException;
import com.clinicops.ops.availability.model.UpdateAvailabilityExceptionRequest;

@Service
public interface DoctorAvailabilityExceptionService {

	DoctorAvailabilityException create(CreateAvailabilityExceptionRequest request);

	DoctorAvailabilityException update(ObjectId id, UpdateAvailabilityExceptionRequest request);

	void delete(ObjectId id);

	List<DoctorAvailabilityException> getByDoctorAndRange(ObjectId doctorId, LocalDate from, LocalDate to);
}
