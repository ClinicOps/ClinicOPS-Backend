package com.clinicops.ops.availability.service;

import java.time.LocalDate;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.ops.appointment.model.Appointment;
import com.clinicops.ops.appointment.repository.AppointmentRepository;
import com.clinicops.ops.availability.dto.CreateAvailabilityRequest;
import com.clinicops.ops.availability.dto.UpdateAvailabilityRequest;
import com.clinicops.ops.availability.model.DoctorAvailability;
import com.clinicops.ops.availability.model.DoctorAvailabilityAudit;
import com.clinicops.ops.availability.repository.DoctorAvailabilityAuditRepository;
import com.clinicops.ops.availability.repository.DoctorAvailabilityRepository;
import com.clinicops.ops.doctor.model.ClinicDoctor;
import com.clinicops.ops.doctor.repository.ClinicDoctorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

	private final DoctorAvailabilityRepository availabilityRepository;
	private final ClinicDoctorRepository clinicDoctorRepository;
	private final AppointmentRepository appointmentRepository;
	private final DoctorAvailabilityAuditRepository availabilityAuditRepository;

	@Override
	public DoctorAvailability createAvailability(CreateAvailabilityRequest request) {

		ObjectId clinicId = new ObjectId(request.getClinicId()); // from security context

		// 1️⃣ Validate ClinicDoctor exists
		ClinicDoctor clinicDoctor = clinicDoctorRepository
				.findByClinicIdAndDoctorIdAndArchivedFalse(clinicId, request.getDoctorId())
				.orElseThrow(() -> new BusinessException("Doctor not assigned to clinic"));

		if (Boolean.FALSE.equals(clinicDoctor.getAvailable())) {
			throw new BusinessException("Doctor is marked unavailable at clinic level");
		}

		// 2️⃣ Basic validation
		if (request.getStartTime().isAfter(request.getEndTime())
				|| request.getStartTime().equals(request.getEndTime())) {
			throw new BusinessException("Invalid time range");
		}

		if (request.getSlotDurationMinutes() <= 0) {
			throw new BusinessException("Slot duration must be positive");
		}

		if (request.getBufferMinutes() < 0) {
			throw new BusinessException("Buffer cannot be negative");
		}

		// 3️⃣ Overlap validation
		List<DoctorAvailability> overlaps = availabilityRepository.findOverlappingAvailability(clinicId,
				request.getDoctorId(), request.getDayOfWeek(), request.getStartTime(), request.getEndTime());

		if (!overlaps.isEmpty()) {
			throw new BusinessException("Availability overlaps with existing schedule");
		}

		// 4️⃣ Create entity
		DoctorAvailability availability = new DoctorAvailability();

		availability.setClinicId(clinicId);
		availability.setDoctorId(request.getDoctorId());
		availability.setDayOfWeek(request.getDayOfWeek());

		availability.setStartTime(request.getStartTime());
		availability.setEndTime(request.getEndTime());

		availability.setSlotDurationMinutes(request.getSlotDurationMinutes());
		availability.setBufferMinutes(request.getBufferMinutes());

		availability.setValidFrom(request.getValidFrom());
		availability.setValidTo(request.getValidTo());

		availability.setIsActive(true);

		DoctorAvailability saved = availabilityRepository.save(availability);

		audit(saved,
		      "CREATE",
		      "Created schedule for " + saved.getDayOfWeek() +
		      " from " + saved.getStartTime() +
		      " to " + saved.getEndTime());

		return saved;
	}

	@Override
	public DoctorAvailability updateAvailability(ObjectId id, UpdateAvailabilityRequest request) {

		ObjectId clinicId = new ObjectId(request.getClinicId());

		DoctorAvailability existing = availabilityRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Availability not found"));

		if (!existing.getClinicId().equals(clinicId)) {
			throw new BusinessException("Access denied");
		}

		if (!existing.getIsActive()) {
			throw new BusinessException("Cannot modify inactive availability");
		}

		// 1️⃣ Basic validation
		if (request.getStartTime().isAfter(request.getEndTime())
				|| request.getStartTime().equals(request.getEndTime())) {
			throw new BusinessException("Invalid time range");
		}

		if (request.getSlotDurationMinutes() <= 0) {
			throw new BusinessException("Slot duration must be positive");
		}

		if (request.getBufferMinutes() < 0) {
			throw new BusinessException("Buffer cannot be negative");
		}

		// 2️⃣ Overlap validation (exclude itself)
		List<DoctorAvailability> overlaps = availabilityRepository.findOverlappingAvailabilityExcludingId(clinicId,
				existing.getDoctorId(), request.getDayOfWeek(), request.getStartTime(), request.getEndTime(), id);

		if (!overlaps.isEmpty()) {
			throw new BusinessException("Availability overlaps with existing schedule");
		}

		// 3️⃣ Future appointment conflict detection
		LocalDate today = LocalDate.now();

		List<Appointment> conflicts = appointmentRepository.findFutureAppointmentsInTimeRange(clinicId,
				existing.getDoctorId(), today, request.getStartTime(), request.getEndTime());

		if (!conflicts.isEmpty()) {
			throw new BusinessException("Cannot modify availability. Future appointments exist in this time range.");
		}

		// 4️⃣ Apply updates
		existing.setDayOfWeek(request.getDayOfWeek());
		existing.setStartTime(request.getStartTime());
		existing.setEndTime(request.getEndTime());
		existing.setSlotDurationMinutes(request.getSlotDurationMinutes());
		existing.setBufferMinutes(request.getBufferMinutes());
		existing.setValidFrom(request.getValidFrom());
		existing.setValidTo(request.getValidTo());

		existing.setVersion(request.getVersion());
		
		String summary = "Updated schedule: "
		        + existing.getDayOfWeek()
		        + " to "
		        + request.getStartTime()
		        + " - "
		        + request.getEndTime();
		
		DoctorAvailability saved = availabilityRepository.save(existing);
		
		audit(saved, "UPDATE", summary);

		return saved;
	}

	@Override
	public void deactivateAvailability(ObjectId id) {

		DoctorAvailability existing = availabilityRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Availability not found"));

		ObjectId clinicId = existing.getClinicId();

		if (!existing.getClinicId().equals(clinicId)) {
			throw new BusinessException("Access denied");
		}

		LocalDate today = LocalDate.now();

		boolean hasFutureAppointments = appointmentRepository
				.existsByClinicIdAndDoctorIdAndAppointmentDateGreaterThanEqual(clinicId, existing.getDoctorId(), today);

		if (hasFutureAppointments) {
			throw new BusinessException("Cannot deactivate availability. Future appointments exist.");
		}

		existing.setIsActive(false);
		availabilityRepository.save(existing);
	}

	private void audit(DoctorAvailability availability, String action, String summary) {

		var auth = SecurityContextHolder.getContext().getAuthentication();

		ObjectId performedBy = null;

		if (auth != null && ObjectId.isValid(auth.getName())) {
			performedBy = new ObjectId(auth.getName());
		}

		DoctorAvailabilityAudit audit = new DoctorAvailabilityAudit();

		audit.setAvailabilityId(availability.getId());
		audit.setClinicId(availability.getClinicId());
		audit.setDoctorId(availability.getDoctorId());
		audit.setAction(action);
		audit.setPerformedBy(performedBy);
		audit.setSummary(summary);

		availabilityAuditRepository.save(audit);
	}
}
