package com.clinicops.modules.doctor.service;

import java.util.List;

import com.clinicops.modules.doctor.dto.DoctorCreateRequest;
import com.clinicops.modules.doctor.dto.DoctorResponse;
import com.clinicops.security.model.AuthUser;

public interface DoctorService {

    DoctorResponse registerDoctor(
        AuthUser actor,
        DoctorCreateRequest request
    );

    List<DoctorResponse> listDoctors(AuthUser actor);
}
