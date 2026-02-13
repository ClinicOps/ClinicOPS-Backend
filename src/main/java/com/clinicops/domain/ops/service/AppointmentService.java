package com.clinicops.domain.ops.service;

import com.clinicops.domain.ops.model.Appointment;
import com.clinicops.domain.ops.repository.AppointmentRepository;
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

        Appointment appointment =
                new Appointment(clinicId, patientName, scheduledAt);

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
        return repository.findByClinicId(clinicId);
    }

    public Appointment get(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }
}
