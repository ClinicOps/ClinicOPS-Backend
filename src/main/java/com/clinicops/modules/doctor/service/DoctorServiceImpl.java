package com.clinicops.modules.doctor.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.common.exception.NotFoundException;
import com.clinicops.modules.auth.model.User;
import com.clinicops.modules.auth.repo.UserRepository;
import com.clinicops.modules.doctor.dto.DoctorCreateRequest;
import com.clinicops.modules.doctor.dto.DoctorResponse;
import com.clinicops.modules.doctor.model.Doctor;
import com.clinicops.modules.doctor.repo.DoctorRepository;
import com.clinicops.security.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Override
    public DoctorResponse registerDoctor(
            AuthUser actor,
            DoctorCreateRequest request) {

        User user = userRepository
            .findById(request.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getWorkspaceId().equals(actor.getWorkspaceId())) {
            throw new BusinessException("Cross-workspace access denied");
        }

        if (doctorRepository.findByUserId(user.getId()).isPresent()) {
            throw new BusinessException("Doctor already registered");
        }

        Doctor doctor = new Doctor();
        doctor.setWorkspaceId(actor.getWorkspaceId());
        doctor.setUserId(user.getId());
        doctor.setFullName(request.getFullName());
        doctor.setRegistrationNumber(request.getRegistrationNumber());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setActive(true);
        doctor.setCreatedAt(Instant.now());

        doctorRepository.save(doctor);

        return toResponse(doctor);
    }

    @Override
    public List<DoctorResponse> listDoctors(AuthUser actor) {

        return doctorRepository
            .findByWorkspaceIdAndActiveTrue(actor.getWorkspaceId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private DoctorResponse toResponse(Doctor doctor) {
        return new DoctorResponse(
            doctor.getId(),
            doctor.getFullName(),
            doctor.getSpecialization(),
            doctor.isActive()
        );
    }
}

