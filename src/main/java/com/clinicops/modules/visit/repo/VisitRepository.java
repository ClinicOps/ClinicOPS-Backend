package com.clinicops.modules.visit.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.visit.model.Visit;

public interface VisitRepository extends MongoRepository<Visit, String> {

	List<Visit> findByWorkspaceIdAndPatientIdOrderByVisitTimeDesc(String workspaceId, String patientId);

	Optional<Visit> findByAppointmentId(String appointmentId);
}
