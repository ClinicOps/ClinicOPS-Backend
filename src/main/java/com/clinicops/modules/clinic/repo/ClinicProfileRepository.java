package com.clinicops.modules.clinic.repo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.clinic.model.ClinicProfile;

public interface ClinicProfileRepository
extends MongoRepository<ClinicProfile, String> {

Optional<ClinicProfile> findByWorkspaceId(String workspaceId);
}