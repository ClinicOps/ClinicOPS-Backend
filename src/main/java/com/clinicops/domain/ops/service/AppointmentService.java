package com.clinicops.domain.ops.service;

import com.clinicops.domain.ops.model.Appointment;
import com.clinicops.domain.ops.repository.AppointmentRepository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository repository;

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
    }

    public Appointment create(
            String clinicId,
            String patientName,
            Instant scheduledAt) {
    	
    	ObjectId clinicObjId = new ObjectId(clinicId);

        Appointment appointment =
                new Appointment(clinicObjId, patientName, scheduledAt);

        return repository.save(appointment);
    }

    public void reschedule(
            Appointment appointment,
            Instant newTime) {

        appointment.reschedule(newTime);
        repository.save(appointment);
    }

    public void cancel(Appointment appointment) {
        appointment.cancel();
        repository.save(appointment);
    }

    public List<Appointment> list(String clinicId) {
        return repository.findByClinicId(new ObjectId(clinicId));
    }

    public Appointment get(String id) {
    	ObjectId objId = new ObjectId(id);
        return repository.findById(objId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }
}
