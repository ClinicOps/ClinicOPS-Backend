package com.clinicops.modules.clinic.service;

import com.clinicops.modules.clinic.dto.ClinicProfileRequest;
import com.clinicops.modules.clinic.dto.ClinicProfileResponse;
import com.clinicops.security.model.AuthUser;

public interface ClinicService {
    ClinicProfileResponse getProfile(AuthUser user);
    ClinicProfileResponse upsertProfile(AuthUser user, ClinicProfileRequest request);
}