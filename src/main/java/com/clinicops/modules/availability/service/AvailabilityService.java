package com.clinicops.modules.availability.service;

import com.clinicops.modules.availability.dto.AvailabilityOverrideRequest;
import com.clinicops.modules.availability.dto.DoctorAvailabilityResponse;
import com.clinicops.modules.availability.dto.WeeklyAvailabilityRequest;
import com.clinicops.security.model.AuthUser;

public interface AvailabilityService {

    DoctorAvailabilityResponse getAvailability(
        AuthUser user,
        String doctorId
    );

    DoctorAvailabilityResponse setWeeklyAvailability(
        AuthUser user,
        String doctorId,
        WeeklyAvailabilityRequest request
    );

    DoctorAvailabilityResponse addOverride(
        AuthUser user,
        String doctorId,
        AvailabilityOverrideRequest request
    );
}
