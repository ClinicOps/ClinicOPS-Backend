package com.clinicops.modules.appointment.repo;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.modules.appointment.model.Appointment;
import com.clinicops.modules.appointment.model.AppointmentStatus;

public interface AppointmentRepository
extends MongoRepository<Appointment, String> {

boolean existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(
String doctorId,
Instant end,
Instant start,
AppointmentStatus status
);

List<Appointment> findByDoctorIdAndStartTimeBetween(
String doctorId,
Instant dayStart,
Instant dayEnd
);
}
