package com.clinicops.modules.availability.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.common.exception.NotFoundException;
import com.clinicops.modules.availability.dto.AvailabilityOverrideRequest;
import com.clinicops.modules.availability.dto.DoctorAvailabilityResponse;
import com.clinicops.modules.availability.dto.WeeklyAvailabilityRequest;
import com.clinicops.modules.availability.model.AvailabilityOverride;
import com.clinicops.modules.availability.model.DoctorAvailability;
import com.clinicops.modules.availability.model.WeeklyAvailability;
import com.clinicops.modules.availability.repo.DoctorAvailabilityRepository;
import com.clinicops.modules.doctor.model.Doctor;
import com.clinicops.modules.doctor.repo.DoctorRepository;
import com.clinicops.security.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final DoctorAvailabilityRepository repository;
    private final DoctorRepository doctorRepository;

    @Override
    public DoctorAvailabilityResponse getAvailability(
            AuthUser user,
            String doctorId) {

        validateDoctor(user, doctorId);

        DoctorAvailability availability = repository
            .findByDoctorId(doctorId)
            .orElse(null);

        if (availability == null) {
            return new DoctorAvailabilityResponse(
                doctorId,
                null,
                List.of()
            );
        }

        return toResponse(availability);
    }

    @Override
    public DoctorAvailabilityResponse setWeeklyAvailability(
            AuthUser user,
            String doctorId,
            WeeklyAvailabilityRequest request) {

        validateDoctor(user, doctorId);

        DoctorAvailability availability = repository
            .findByDoctorId(doctorId)
            .orElseGet(() -> createNew(user, doctorId));

        availability.setWeekly(
            new WeeklyAvailability(request.getDays())
        );
        availability.setUpdatedAt(Instant.now());

        repository.save(availability);
        return toResponse(availability);
    }

    @Override
    public DoctorAvailabilityResponse addOverride(
            AuthUser user,
            String doctorId,
            AvailabilityOverrideRequest request) {

        validateDoctor(user, doctorId);

        DoctorAvailability availability = repository
            .findByDoctorId(doctorId)
            .orElseGet(() -> createNew(user, doctorId));

        AvailabilityOverride override = new AvailabilityOverride();
        override.setDate(request.getDate());
        override.setAvailableSlots(request.getAvailableSlots());
        override.setReason(request.getReason());

        availability.getOverrides().add(override);
        availability.setUpdatedAt(Instant.now());

        repository.save(availability);
        return toResponse(availability);
    }

    // helpers
    
    private void validateDoctor(AuthUser user, String doctorId) {

        Doctor doctor = doctorRepository
            .findById(doctorId)
            .orElseThrow(() -> new NotFoundException("Doctor not found"));

        if (!doctor.getWorkspaceId().equals(user.getWorkspaceId())) {
            throw new BusinessException("Cross-workspace access denied");
        }
    }

    private DoctorAvailability createNew(AuthUser user, String doctorId) {

        DoctorAvailability a = new DoctorAvailability();
        a.setWorkspaceId(user.getWorkspaceId());
        a.setDoctorId(doctorId);
        return a;
    }

    private DoctorAvailabilityResponse toResponse(DoctorAvailability a) {
        return new DoctorAvailabilityResponse(
            a.getDoctorId(),
            a.getWeekly(),
            a.getOverrides()
        );
    }

}
