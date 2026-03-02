package com.clinicops.ops.appointment.service;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.ops.appointment.model.Appointment;
import com.clinicops.ops.appointment.model.RescheduleAppointmentRequest;
import com.clinicops.ops.appointment.repository.AppointmentRepository;
import com.clinicops.ops.availability.dto.SlotDTO;
import com.clinicops.ops.availability.model.SlotStatus;
import com.clinicops.ops.patient.model.Patient;
import com.clinicops.ops.patient.repository.PatientRepository;
import com.clinicops.security.SecurityUtils;
import com.mongodb.DuplicateKeyException;

@Service
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;

	public AppointmentService(AppointmentRepository repository, PatientRepository patientRepository) {
		this.appointmentRepository = repository;
		this.patientRepository = patientRepository;
	}

	public Appointment create(ObjectId objectId, String patientIdStr, Instant scheduledAt) {

		if (!ObjectId.isValid(patientIdStr)) {
			throw new IllegalArgumentException("Invalid ID");
		}

		ObjectId clinicId = objectId;
		ObjectId patientId = new ObjectId(patientIdStr);

		Patient patient = patientRepository.findByClinicIdAndId(clinicId, patientId)
				.orElseThrow(() -> new IllegalStateException("Patient not found"));

		if (!"ACTIVE".equals(patient.getStatus())) {
			throw new IllegalStateException("Cannot create appointment for archived patient");
		}

		String snapshotName = patient.getPersonal().getFirstName() + " " + patient.getPersonal().getLastName();

		Appointment appointment = new Appointment(clinicId, patientId, snapshotName, scheduledAt);

//        List<SlotDTO> slots =
//                slotGenerationService.generateSlots(
//                        clinicId,
//                        request.getDoctorId(),
//                        request.getAppointmentDate()
//                );
//
//        Optional<SlotDTO> requestedSlot =
//                slots.stream()
//                     .filter(s -> s.getStart().equals(request.getStartTime()))
//                     .findFirst();
//
//        if (requestedSlot.isEmpty()) {
//            throw new BusinessException("Invalid slot selected");
//        }
//
//        if (requestedSlot.get().getStatus() != SlotStatus.AVAILABLE) {
//            throw new BusinessException("Slot is not available");
//        }

		try {
			return appointmentRepository.save(appointment);
		} catch (DuplicateKeyException ex) {
			throw new BusinessException("Slot already booked");
		}
	}

//	@Override
//	public Appointment reschedule(ObjectId appointmentId,
//	                              RescheduleAppointmentRequest request) {
//
//	    ObjectId clinicId = SecurityUtils.getCurrentClinicId();
//
//	    Appointment appointment =
//	            appointmentRepository.findById(appointmentId)
//	                    .orElseThrow(() ->
//	                            new BusinessException("Appointment not found"));
//
//	    if (!appointment.getClinicId().equals(clinicId)) {
//	        throw new BusinessException("Access denied");
//	    }
//
//	    if (!appointment.isActive()) {
//	        throw new BusinessException("Cannot reschedule cancelled appointment");
//	    }
//
//	    if (request.getNewDate().isBefore(LocalDate.now())) {
//	        throw new BusinessException("Cannot reschedule to past date");
//	    }
//
//	    // 🔒 Validate slot via availability engine
//	    List<SlotDTO> slots =
//	            slotGenerationService.generateSlots(
//	                    clinicId,
//	                    appointment.getDoctorId(),
//	                    request.getNewDate()
//	            );
//
//	    SlotDTO targetSlot =
//	            slots.stream()
//	                 .filter(s -> s.getStart().equals(request.getNewStartTime()))
//	                 .findFirst()
//	                 .orElseThrow(() ->
//	                        new BusinessException("Invalid slot selected"));
//
//	    if (targetSlot.getStatus() != SlotStatus.AVAILABLE) {
//	        throw new BusinessException("Selected slot is not available");
//	    }
//
//	    // Extract duration + buffer from generated slot context
//	    int duration =
//	            (int) ChronoUnit.MINUTES.between(
//	                    targetSlot.getStart(),
//	                    targetSlot.getEnd()
//	            );
//
//	    int buffer = 0; // currently not exposed in SlotDTO (we can improve later)
//
//	    try {
//
//	        appointment.reschedule(
//	                request.getNewDate(),
//	                request.getNewStartTime(),
//	                duration,
//	                buffer,
//	                ZoneId.systemDefault()
//	        );
//
//	        return appointmentRepository.save(appointment);
//
//	    } catch (DuplicateKeyException ex) {
//	        throw new BusinessException("Slot already booked");
//	    }
//	}

	public void cancel(Appointment appointment) {
		appointment.cancel();
		appointmentRepository.save(appointment);
	}

	public List<Appointment> list(ObjectId clinicId) {
		return appointmentRepository.findByClinicId(clinicId);
	}

	public Appointment get(String id) {
		ObjectId objId = new ObjectId(id);
		return appointmentRepository.findById(objId).orElseThrow(() -> new RuntimeException("Appointment not found"));
	}
}
