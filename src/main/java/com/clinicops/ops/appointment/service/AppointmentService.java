package com.clinicops.ops.appointment.service;

import com.clinicops.domain.ops.model.Patient;
import com.clinicops.domain.ops.repository.PatientRepository;
import com.clinicops.ops.appointment.model.Appointment;
import com.clinicops.ops.appointment.repository.AppointmentRepository;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository repository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository repository, PatientRepository patientRepository) {
        this.repository = repository;
        this.patientRepository = patientRepository;
    }

    public Appointment create(
            String clinicIdStr,
            String patientIdStr,
            Instant scheduledAt) {

        if (!ObjectId.isValid(clinicIdStr) ||
            !ObjectId.isValid(patientIdStr)) {
            throw new IllegalArgumentException("Invalid ID");
        }

        ObjectId clinicId = new ObjectId(clinicIdStr);
        ObjectId patientId = new ObjectId(patientIdStr);

        Patient patient = patientRepository
                .findByClinicIdAndId(clinicId, patientId)
                .orElseThrow(() ->
                    new IllegalStateException("Patient not found"));

        if (!"ACTIVE".equals(patient.getStatus())) {
            throw new IllegalStateException(
                "Cannot create appointment for archived patient");
        }

        String snapshotName =
                patient.getPersonal().getFirstName() + " " +
                patient.getPersonal().getLastName();

        Appointment appointment = new Appointment(
                clinicId,
                patientId,
                snapshotName,
                scheduledAt
        );

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
