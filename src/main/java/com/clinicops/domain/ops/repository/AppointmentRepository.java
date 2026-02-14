package com.clinicops.domain.ops.repository;

import com.clinicops.domain.ops.model.Appointment;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AppointmentRepository
        extends MongoRepository<Appointment, ObjectId> {

    List<Appointment> findByClinicId(ObjectId clinicId);
}
