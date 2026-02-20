package com.clinicops.domain.ops.repository;


import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.clinicops.domain.ops.model.Appointment;
import com.clinicops.domain.ops.model.AppointmentStatus;

public interface AppointmentRepository
        extends MongoRepository<Appointment, ObjectId> {

    List<Appointment> findByClinicId(ObjectId clinicId);
    
    Page<Appointment> findByClinicIdAndStatusNot(
            ObjectId clinicId,
            AppointmentStatus status,
            Pageable pageable
    );
}
