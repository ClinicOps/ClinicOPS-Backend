package com.clinicops.ops.availability.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.ops.appointment.model.Appointment;
import com.clinicops.ops.appointment.repository.AppointmentRepository;
import com.clinicops.ops.availability.dto.SlotDTO;
import com.clinicops.ops.availability.model.DailySlotsDTO;
import com.clinicops.ops.availability.model.DoctorAvailability;
import com.clinicops.ops.availability.model.DoctorAvailabilityException;
import com.clinicops.ops.availability.model.ExceptionType;
import com.clinicops.ops.availability.model.SlotStatus;
import com.clinicops.ops.availability.repository.DoctorAvailabilityExceptionRepository;
import com.clinicops.ops.availability.repository.DoctorAvailabilityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlotGenerationServiceImpl implements SlotGenerationService {

	private final DoctorAvailabilityRepository availabilityRepository;
	private final DoctorAvailabilityExceptionRepository exceptionRepository;
	private final AppointmentRepository appointmentRepository;

	@Override
	public List<SlotDTO> generateSlots(ObjectId clinicId, ObjectId doctorId, LocalDate date) {

		LocalDate today = LocalDate.now();

		// 1️⃣ Fetch availability rules
		List<DoctorAvailability> rules = availabilityRepository
				.findByClinicIdAndDoctorIdAndDayOfWeekAndIsActiveTrue(clinicId, doctorId, date.getDayOfWeek()).stream()
				.filter(rule -> !date.isBefore(rule.getValidFrom())
						&& (rule.getValidTo() == null || !date.isAfter(rule.getValidTo())))
				.toList();

		if (rules.isEmpty()) {
			return List.of();
		}

		// 2️⃣ Fetch exceptions
		List<DoctorAvailabilityException> exceptions = exceptionRepository.findByClinicIdAndDoctorIdAndDate(clinicId,
				doctorId, date);

		// 3️⃣ Fetch appointments
		List<Appointment> appointments = appointmentRepository.findByClinicIdAndDoctorIdAndAppointmentDate(clinicId,
				doctorId, date);

		Map<LocalTime, Appointment> bookedMap = appointments.stream().filter(Appointment::isActive)
				.collect(Collectors.toMap(Appointment::getStartTime, a -> a));

		List<SlotDTO> slots = new ArrayList<>();

		// 4️⃣ Generate from recurring rules
		for (DoctorAvailability rule : rules) {

			LocalTime cursor = rule.getStartTime();

			while (!cursor.plusMinutes(rule.getSlotDurationMinutes()).isAfter(rule.getEndTime())) {

				LocalTime slotEnd = cursor.plusMinutes(rule.getSlotDurationMinutes());

				SlotDTO slot = new SlotDTO();
				slot.setStart(cursor);
				slot.setEnd(slotEnd);

				slot.setStatus(resolveStatus(date, cursor, slotEnd, today, bookedMap, exceptions));

				slots.add(slot);

				cursor = slotEnd.plusMinutes(rule.getBufferMinutes());
			}
		}

		int defaultDuration = 15;
		int defaultBuffer = 0;

		if (!rules.isEmpty()) {
			// Use first active rule as reference
			DoctorAvailability refRule = rules.get(0);
			defaultDuration = refRule.getSlotDurationMinutes();
			defaultBuffer = refRule.getBufferMinutes();
		}

		// 5️⃣ Add EXTRA exceptions
		for (DoctorAvailabilityException ex : exceptions) {

			if (ex.getType() == ExceptionType.EXTRA) {

				LocalTime start = ex.getStartTime();
				LocalTime end = ex.getEndTime();

				if (start == null || end == null)
					continue;

				LocalTime cursor = start;

				while (!cursor.plusMinutes(defaultDuration).isAfter(end)) {

					LocalTime slotEnd = cursor.plusMinutes(defaultDuration);

					final LocalTime slotStartRef = cursor;

					boolean exists = false;

					for (SlotDTO s : slots) {
						if (s.getStart().equals(slotStartRef)) {
							exists = true;
							break;
						}
					}

					if (!exists) {

						SlotDTO extraSlot = new SlotDTO();
						extraSlot.setStart(slotStartRef);
						extraSlot.setEnd(slotEnd);

						extraSlot.setStatus(resolveStatus(date, slotStartRef, slotEnd, today, bookedMap, exceptions));

						slots.add(extraSlot);
					}

					cursor = slotEnd.plusMinutes(defaultBuffer);
				}
			}
		}

		slots.sort(Comparator.comparing(SlotDTO::getStart));

		return slots;
	}

	private SlotStatus resolveStatus(LocalDate date, LocalTime start, LocalTime end, LocalDate today,
			Map<LocalTime, Appointment> bookedMap, List<DoctorAvailabilityException> exceptions) {

		if (date.isBefore(today)) {
			return SlotStatus.EXPIRED;
		}

		// Check blocked / leave
		for (DoctorAvailabilityException ex : exceptions) {

			if (ex.getType() == ExceptionType.BLOCKED || ex.getType() == ExceptionType.LEAVE) {

				if (ex.getStartTime() == null || overlaps(start, end, ex.getStartTime(), ex.getEndTime())) {
					return SlotStatus.BLOCKED;
				}
			}
		}

		if (bookedMap.containsKey(start)) {
			return SlotStatus.BOOKED;
		}

		return SlotStatus.AVAILABLE;
	}

	private boolean overlaps(LocalTime s1, LocalTime e1, LocalTime s2, LocalTime e2) {

		return s1.isBefore(e2) && e1.isAfter(s2);
	}

	@Override
	public List<DailySlotsDTO> generateCalendar(ObjectId clinicId, ObjectId doctorId, LocalDate from, LocalDate to) {

		if (from == null || to == null) {
			throw new BusinessException("Date range required");
		}

		if (to.isBefore(from)) {
			throw new BusinessException("Invalid date range");
		}

		long days = ChronoUnit.DAYS.between(from, to);

		if (days > 30) {
			throw new BusinessException("Maximum 30 days allowed");
		}

		List<DailySlotsDTO> result = new ArrayList<>();

		LocalDate cursor = from;

		while (!cursor.isAfter(to)) {

			List<SlotDTO> slots = generateSlots(clinicId, doctorId, cursor);

			result.add(new DailySlotsDTO(cursor, slots));

			cursor = cursor.plusDays(1);
		}

		return result;
	}
}
