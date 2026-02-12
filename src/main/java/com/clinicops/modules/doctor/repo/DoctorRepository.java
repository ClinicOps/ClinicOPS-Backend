package com.clinicops.modules.doctor.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.doctor.model.Doctor;

public interface DoctorRepository
extends MongoRepository<Doctor, String> {

Optional<Doctor> findByUserId(String userId);

List<Doctor> findByWorkspaceIdAndActiveTrue(String workspaceId);
}
