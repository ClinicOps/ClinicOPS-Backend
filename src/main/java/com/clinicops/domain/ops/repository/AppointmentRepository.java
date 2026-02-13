package com.clinicops.domain.ops.repository;

import com.clinicops.domain.ops.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AppointmentRepository
        extends MongoRepository<Appointment, String> {

    List<Appointment> findByClinicId(String clinicId);
}
