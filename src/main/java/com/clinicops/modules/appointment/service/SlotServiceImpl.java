package com.clinicops.modules.appointment.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.common.exception.NotFoundException;
import com.clinicops.modules.appointment.dto.SlotResponse;
import com.clinicops.modules.appointment.model.Appointment;
import com.clinicops.modules.appointment.repo.AppointmentRepository;
import com.clinicops.modules.availability.model.AvailabilityOverride;
import com.clinicops.modules.availability.model.DoctorAvailability;
import com.clinicops.modules.availability.model.TimeRange;
import com.clinicops.modules.availability.model.WeeklyAvailability;
import com.clinicops.modules.availability.repo.DoctorAvailabilityRepository;
import com.clinicops.modules.doctor.model.Doctor;
import com.clinicops.modules.doctor.repo.DoctorRepository;
import com.clinicops.security.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final DoctorAvailabilityRepository availabilityRepo;
    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;

    @Override
    public List<SlotResponse> getAvailableSlots(
            AuthUser user,
            String doctorId,
            LocalDate date,
            int slotMinutes) {

        Doctor doctor = doctorRepo.findById(doctorId)
            .orElseThrow(() -> new NotFoundException("Doctor not found"));

        if (!doctor.getWorkspaceId().equals(user.getWorkspaceId())) {
            throw new BusinessException("Cross-workspace access denied");
        }

        DoctorAvailability availability =
            availabilityRepo.findByDoctorId(doctorId).orElse(null);

        if (availability == null) return List.of();

        List<TimeRange> ranges =
            resolveAvailabilityForDate(availability, date);

        if (ranges.isEmpty()) return List.of();

        Instant dayStart = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant dayEnd = dayStart.plus(1, ChronoUnit.DAYS);

        List<Appointment> appointments =
            appointmentRepo.findByDoctorIdAndStartTimeBetween(
                doctorId, dayStart, dayEnd
            );

        return generateSlots(ranges, appointments, slotMinutes, date);
    }

    // helpers below
    
    private List<TimeRange> resolveAvailabilityForDate(
            DoctorAvailability availability,
            LocalDate date) {

        // 1️⃣ Check override first
        for (AvailabilityOverride o : availability.getOverrides()) {
            if (o.getDate().equals(date)) {
                return o.getAvailableSlots();
            }
        }

        // 2️⃣ Fallback to weekly
        WeeklyAvailability weekly = availability.getWeekly();
        if (weekly == null) return List.of();

        return weekly.getDays()
            .getOrDefault(date.getDayOfWeek(), List.of());
    }
    
    private List<SlotResponse> generateSlots(
            List<TimeRange> availability,
            List<Appointment> appointments,
            int slotMinutes,
            LocalDate date) {

        List<SlotResponse> slots = new ArrayList<>();

        for (TimeRange range : availability) {

            LocalTime cursor = range.getStart();

            while (cursor.plusMinutes(slotMinutes).isBefore(range.getEnd())
                   || cursor.plusMinutes(slotMinutes).equals(range.getEnd())) {

                Instant slotStart =
                    ZonedDateTime.of(date, cursor, ZoneOffset.UTC).toInstant();

                Instant slotEnd =
                    slotStart.plus(slotMinutes, ChronoUnit.MINUTES);

                boolean overlaps =
                    appointments.stream().anyMatch(a ->
                        slotStart.isBefore(a.getEndTime()) &&
                        slotEnd.isAfter(a.getStartTime())
                    );

                if (!overlaps) {
                    slots.add(new SlotResponse(slotStart, slotEnd));
                }

                cursor = cursor.plusMinutes(slotMinutes);
            }
        }

        return slots;
    }


}
