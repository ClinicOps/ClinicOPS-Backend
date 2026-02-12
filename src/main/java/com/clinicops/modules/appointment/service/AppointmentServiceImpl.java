package com.clinicops.modules.appointment.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.clinicops.common.exception.BusinessException;
import com.clinicops.common.exception.NotFoundException;
import com.clinicops.infra.messaging.events.AppointmentBookedEvent;
import com.clinicops.infra.messaging.events.AppointmentCancelledEvent;
import com.clinicops.infra.messaging.events.AppointmentRescheduledEvent;
import com.clinicops.infra.messaging.publisher.RabbitEventPublisher;
import com.clinicops.infra.redis.RedisLockService;
import com.clinicops.modules.appointment.dto.AppointmentCancelRequest;
import com.clinicops.modules.appointment.dto.AppointmentCreateRequest;
import com.clinicops.modules.appointment.dto.AppointmentRescheduleRequest;
import com.clinicops.modules.appointment.dto.AppointmentResponse;
import com.clinicops.modules.appointment.model.Appointment;
import com.clinicops.modules.appointment.model.AppointmentStatus;
import com.clinicops.modules.appointment.repo.AppointmentRepository;
import com.clinicops.modules.audit.service.AuditService;
import com.clinicops.modules.doctor.model.Doctor;
import com.clinicops.modules.doctor.repo.DoctorRepository;
import com.clinicops.security.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final RedisLockService lockService;
    private final RabbitEventPublisher eventPublisher;
    private final AuditService auditService;

    private static final Duration LOCK_TTL = Duration.ofSeconds(45);

    @Override
    public AppointmentResponse bookAppointment(
            AuthUser user,
            AppointmentCreateRequest request) {

        // 1️⃣ Doctor validation
        Doctor doctor = doctorRepository
            .findById(request.getDoctorId())
            .orElseThrow(() -> new NotFoundException("Doctor not found"));

        if (!doctor.getWorkspaceId().equals(user.getWorkspaceId())) {
            throw new BusinessException("Cross-workspace access denied");
        }

        // 2️⃣ Build lock key (doctor + slot)
        String lockKey = buildLockKey(
            request.getDoctorId(),
            request.getStartTime()
        );

        boolean locked = lockService.acquireLock(lockKey, LOCK_TTL);
        if (!locked) {
            throw new BusinessException("Slot is being booked, try again");
        }

        try {
            // 3️⃣ DB-level overlap check (absolute safety)
            boolean conflict =
                appointmentRepository
                    .existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatus(
                        request.getDoctorId(),
                        request.getEndTime(),
                        request.getStartTime(),
                        AppointmentStatus.BOOKED
                    );

            if (conflict) {
                throw new BusinessException("Slot already booked");
            }

            // 4️⃣ Create appointment
            Appointment appt = new Appointment();
            appt.setWorkspaceId(user.getWorkspaceId());
            appt.setDoctorId(request.getDoctorId());
            appt.setPatientId(request.getPatientId());
            appt.setStartTime(request.getStartTime());
            appt.setEndTime(request.getEndTime());
            appt.setStatus(AppointmentStatus.BOOKED);
            appt.setCreatedAt(Instant.now());

            appointmentRepository.save(appt);
            
            auditService.record(
            	    user.getWorkspaceId(),
            	    user,
            	    "APPOINTMENT_BOOKED",
            	    "APPOINTMENT",
            	    appt.getId(),
            	    Map.of(
            	        "doctorId", appt.getDoctorId(),
            	        "startTime", appt.getStartTime()
            	    )
            	);


            eventPublisher.publish(
                new AppointmentBookedEvent(
                    appt.getId(),
                    appt.getWorkspaceId(),
                    appt.getDoctorId(),
                    appt.getPatientId(),
                    appt.getStartTime(),
                    appt.getEndTime()
                )
            );


            return new AppointmentResponse(
                appt.getId(),
                appt.getDoctorId(),
                appt.getPatientId(),
                appt.getStartTime(),
                appt.getEndTime(),
                appt.getStatus()
            );

        } finally {
            // 5️⃣ Always release lock
            lockService.releaseLock(lockKey);
        }
    }

    private String buildLockKey(String doctorId, Instant startTime) {
        return "lock:appointment:" + doctorId + ":" + startTime.toEpochMilli();
    }
    
    @Override
    public void cancelAppointment(
            AuthUser user,
            String appointmentId,
            AppointmentCancelRequest request) {

        Appointment appt = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new NotFoundException("Appointment not found"));

        if (!appt.getWorkspaceId().equals(user.getWorkspaceId())) {
            throw new BusinessException("Cross-workspace access denied");
        }

        if (appt.getStatus() != AppointmentStatus.BOOKED) {
            throw new BusinessException("Appointment cannot be cancelled");
        }

        appt.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appt);
        
        auditService.record(
        	    user.getWorkspaceId(),
        	    user,
        	    "APPOINTMENT_CANCELLED",
        	    "APPOINTMENT",
        	    appt.getId(),
        	    Map.of(
        	        "doctorId", appt.getDoctorId(),
        	        "startTime", appt.getStartTime()
        	    )
        	);

        eventPublisher.publish(
            new AppointmentCancelledEvent(
                appt.getId(),
                appt.getWorkspaceId(),
                appt.getDoctorId(),
                appt.getPatientId()
            )
        );

    }
    
    @Override
    public AppointmentResponse rescheduleAppointment(
            AuthUser user,
            String appointmentId,
            AppointmentRescheduleRequest request) {

        Appointment existing = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new NotFoundException("Appointment not found"));

        if (!existing.getWorkspaceId().equals(user.getWorkspaceId())) {
            throw new BusinessException("Cross-workspace access denied");
        }

        if (existing.getStatus() != AppointmentStatus.BOOKED) {
            throw new BusinessException("Appointment cannot be rescheduled");
        }

        // 1️⃣ Cancel existing appointment
        existing.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(existing);

        // 2️⃣ Book new appointment using SAME logic
        AppointmentCreateRequest newReq = new AppointmentCreateRequest();
        newReq.setDoctorId(existing.getDoctorId());
        newReq.setPatientId(existing.getPatientId());
        newReq.setStartTime(request.getNewStartTime());
        newReq.setEndTime(request.getNewEndTime());
        
        AppointmentResponse newAppt = bookAppointment(user, newReq);
        
        auditService.record(
        	    user.getWorkspaceId(),
        	    user,
        	    "APPOINTMENT_RESCHEDULED",
        	    "APPOINTMENT",
        	    newAppt.getId(),
        	    Map.of(
        	        "doctorId", newAppt.getDoctorId(),
        	        "startTime", newAppt.getStartTime()
        	    )
        	);

        eventPublisher.publish(
            new AppointmentRescheduledEvent(
                existing.getId(),
                newAppt.getId(),
                user.getWorkspaceId()
            )
        );

        return newAppt;
    }


    
}
