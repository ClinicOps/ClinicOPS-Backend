package com.clinicops.ops.availability.service;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.clinicops.ops.availability.dto.CreateAvailabilityRequest;
import com.clinicops.ops.availability.dto.UpdateAvailabilityRequest;
import com.clinicops.ops.availability.model.DoctorAvailability;

@Service
public interface DoctorAvailabilityService {
	
    public DoctorAvailability createAvailability(CreateAvailabilityRequest request);

    public DoctorAvailability updateAvailability(ObjectId id, UpdateAvailabilityRequest request);

    public void deactivateAvailability(ObjectId id);


}
