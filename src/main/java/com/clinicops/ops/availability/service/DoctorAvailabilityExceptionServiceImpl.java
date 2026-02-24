//package com.clinicops.ops.availability.service;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//
//import org.bson.types.ObjectId;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import com.clinicops.common.exception.BusinessException;
//import com.clinicops.ops.appointment.model.Appointment;
//import com.clinicops.ops.appointment.repository.AppointmentRepository;
//import com.clinicops.ops.availability.model.CreateAvailabilityExceptionRequest;
//import com.clinicops.ops.availability.model.DoctorAvailabilityException;
//import com.clinicops.ops.availability.model.DoctorAvailabilityExceptionAudit;
//import com.clinicops.ops.availability.model.ExceptionType;
//import com.clinicops.ops.availability.model.UpdateAvailabilityExceptionRequest;
//import com.clinicops.ops.availability.repository.DoctorAvailabilityExceptionAuditRepository;
//import com.clinicops.ops.availability.repository.DoctorAvailabilityExceptionRepository;
//import com.clinicops.ops.doctor.repository.ClinicDoctorRepository;
//import com.clinicops.security.SecurityUtils;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class DoctorAvailabilityExceptionServiceImpl implements DoctorAvailabilityExceptionService {
//
//	private final DoctorAvailabilityExceptionRepository repository;
//	private final AppointmentRepository appointmentRepository;
//	private final ClinicDoctorRepository clinicDoctorRepository;
//	private final DoctorAvailabilityExceptionAuditRepository exceptionAuditRepository;
//
//	@Override
//	public DoctorAvailabilityException create(CreateAvailabilityExceptionRequest request) {
//
//		ObjectId clinicId = SecurityUtils.getCurrentClinicId();
//
//		validateBasic(request.getDate(), request.getStartTime(), request.getEndTime(), request.getType());
//
//		clinicDoctorRepository.findByClinicIdAndDoctorIdAndArchivedFalse(clinicId, request.getDoctorId())
//				.orElseThrow(() -> new BusinessException("Doctor not assigned to clinic"));
//
//		// Appointment conflict check
//		if (request.getType() == ExceptionType.BLOCKED || request.getType() == ExceptionType.LEAVE) {
//
//			validateNoAppointments(clinicId, request.getDoctorId(), request.getDate(), request.getStartTime(),
//					request.getEndTime());
//		}
//
//		DoctorAvailabilityException ex = new DoctorAvailabilityException();
//
//		ex.setClinicId(clinicId);
//		ex.setDoctorId(request.getDoctorId());
//		ex.setDate(request.getDate());
//		ex.setStartTime(request.getStartTime());
//		ex.setEndTime(request.getEndTime());
//		ex.setType(request.getType());
//		ex.setReason(request.getReason());
//
//		DoctorAvailabilityException saved = repository.save(ex);
//		
//		audit(saved,
//			      "CREATE",
//			      "Created " + saved.getType() +
//			      " on " + saved.getDate());
//		
//		return saved;
//	}
//
//	@Override
//	public DoctorAvailabilityException update(ObjectId id, UpdateAvailabilityExceptionRequest request) {
//
//		ObjectId clinicId = SecurityUtils.getCurrentClinicId();
//
//		DoctorAvailabilityException existing = repository.findById(id)
//				.orElseThrow(() -> new BusinessException("Exception not found"));
//
//		if (!existing.getClinicId().equals(clinicId)) {
//			throw new BusinessException("Access denied");
//		}
//
//		validateBasic(request.getDate(), request.getStartTime(), request.getEndTime(), request.getType());
//
//		if (request.getType() == ExceptionType.BLOCKED || request.getType() == ExceptionType.LEAVE) {
//
//			validateNoAppointments(clinicId, existing.getDoctorId(), request.getDate(), request.getStartTime(),
//					request.getEndTime());
//		}
//
//		existing.setDate(request.getDate());
//		existing.setStartTime(request.getStartTime());
//		existing.setEndTime(request.getEndTime());
//		existing.setType(request.getType());
//		existing.setReason(request.getReason());
//		existing.setVersion(request.getVersion());
//		
//		DoctorAvailabilityException saved = repository.save(existing);
//		
//		audit(saved,
//			      "UPDATE",
//			      "Updated exception on " + saved.getDate());
//
//		return saved;
//	}
//
//	@Override
//	public void delete(ObjectId id) {
//
//		ObjectId clinicId = SecurityUtils.getCurrentClinicId();
//
//		DoctorAvailabilityException existing = repository.findById(id)
//				.orElseThrow(() -> new BusinessException("Exception not found"));
//
//		if (!existing.getClinicId().equals(clinicId)) {
//			throw new BusinessException("Access denied");
//		}
//		
//		audit(existing,
//			      "DELETE",
//			      "Deleted " + existing.getType() +
//			      " on " + existing.getDate());
//
//		repository.delete(existing);
//	}
//
//	@Override
//	public List<DoctorAvailabilityException> getByDoctorAndRange(ObjectId doctorId, LocalDate from, LocalDate to) {
//
//		ObjectId clinicId = SecurityUtils.getCurrentClinicId();
//
//		return repository.findByClinicIdAndDoctorIdAndDateBetween(clinicId, doctorId, from, to);
//	}
//
//	private void validateBasic(LocalDate date, LocalTime start, LocalTime end, ExceptionType type) {
//
//		if (date == null)
//			throw new BusinessException("Date required");
//
//		if (type == null)
//			throw new BusinessException("Exception type required");
//
//		if (start != null && end == null)
//			throw new BusinessException("End time required");
//
//		if (start != null && !start.isBefore(end))
//			throw new BusinessException("Invalid time range");
//	}
//
//	private void validateNoAppointments(ObjectId clinicId, ObjectId doctorId, LocalDate date, LocalTime start,
//			LocalTime end) {
//
//		List<Appointment> conflicts;
//
//		if (start == null) {
//			conflicts = appointmentRepository.findByClinicIdAndDoctorIdAndAppointmentDate(clinicId, doctorId, date);
//		} else {
//			conflicts = appointmentRepository.findByClinicIdAndDoctorIdAndAppointmentDateAndStartTimeBetween(clinicId,
//					doctorId, date, start, end);
//		}
//
//		if (!conflicts.isEmpty()) {
//			throw new BusinessException("Appointments exist in this period. Cancel or reschedule first.");
//		}
//	}
//
//	private void audit(DoctorAvailabilityException ex, String action, String summary) {
//
//		var auth = SecurityContextHolder.getContext().getAuthentication();
//
//		ObjectId performedBy = null;
//
//		if (auth != null && ObjectId.isValid(auth.getName())) {
//			performedBy = new ObjectId(auth.getName());
//		}
//
//		DoctorAvailabilityExceptionAudit audit = new DoctorAvailabilityExceptionAudit();
//
//		audit.setExceptionId(ex.getId());
//		audit.setClinicId(ex.getClinicId());
//		audit.setDoctorId(ex.getDoctorId());
//		audit.setAction(action);
//		audit.setPerformedBy(performedBy);
//		audit.setSummary(summary);
//
//		exceptionAuditRepository.save(audit);
//	}
//}