package com.clinicops.modules.clinic.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.clinicops.common.exception.NotFoundException;
import com.clinicops.modules.clinic.dto.ClinicProfileRequest;
import com.clinicops.modules.clinic.dto.ClinicProfileResponse;
import com.clinicops.modules.clinic.model.ClinicProfile;
import com.clinicops.modules.clinic.repo.ClinicProfileRepository;
import com.clinicops.security.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {

    private final ClinicProfileRepository repository;

    @Override
    public ClinicProfileResponse getProfile(AuthUser user) {

        ClinicProfile profile = repository
            .findByWorkspaceId(user.getWorkspaceId())
            .orElseThrow(() -> new NotFoundException("Clinic profile not found"));

        return toResponse(profile);
    }

    @Override
    public ClinicProfileResponse upsertProfile(
            AuthUser user,
            ClinicProfileRequest request) {

        ClinicProfile profile = repository
            .findByWorkspaceId(user.getWorkspaceId())
            .orElseGet(() -> {
                ClinicProfile p = new ClinicProfile();
                p.setWorkspaceId(user.getWorkspaceId());
                p.setCreatedAt(Instant.now());
                return p;
            });

        profile.setName(request.getName());
        profile.setAddress(request.getAddress());
        profile.setPhone(request.getPhone());
        profile.setEmail(request.getEmail());
        profile.setUpdatedAt(Instant.now());

        repository.save(profile);
        return toResponse(profile);
    }

    private ClinicProfileResponse toResponse(ClinicProfile profile) {
        return new ClinicProfileResponse(
            profile.getName(),
            profile.getAddress(),
            profile.getPhone(),
            profile.getEmail()
        );
    }
}

