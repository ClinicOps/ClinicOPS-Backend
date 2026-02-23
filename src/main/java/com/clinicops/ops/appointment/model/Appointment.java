package com.clinicops.ops.appointment.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

import lombok.Getter;

@Getter
@Document("appointments")
@CompoundIndexes({ @CompoundIndex(name = "clinic_scheduled_idx", def = "{'clinicId': 1, 'scheduledAt': 1}"),
		@CompoundIndex(name = "clinic_status_idx", def = "{'clinicId': 1, 'status': 1}"),
		@CompoundIndex(name = "clinic_doctor_date_idx", def = "{'clinicId':1,'doctorId':1,'appointmentDate':1}") })
public class Appointment extends BaseEntity {

	private ObjectId clinicId;
	private ObjectId patientId;
	private ObjectId doctorId;
	
	// snapshot for historical accuracy
	private String patientNameSnapshot;
	
	private Instant scheduledAt;
	
	private LocalDate appointmentDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private Integer slotDurationMinutes;
	private Integer bufferMinutes;
	
	private AppointmentStatus status;

	public Appointment(ObjectId clinicId2, ObjectId patientId2, String snapshotName, Instant scheduledAt2) {
		// For Mongo
	}

	public Appointment(ObjectId clinicId, ObjectId patientId, ObjectId doctorId, String patientNameSnapshot,
			LocalDate appointmentDate, LocalTime startTime, Integer slotDurationMinutes, Integer bufferMinutes,
			ZoneId clinicZoneId) {

		if (clinicId == null)
			throw new IllegalArgumentException("ClinicId required");
		if (doctorId == null)
			throw new IllegalArgumentException("Doctor required");
		if (appointmentDate == null)
			throw new IllegalArgumentException("Date required");
		if (startTime == null)
			throw new IllegalArgumentException("Start time required");

		this.clinicId = clinicId;
		this.patientId = patientId;
		this.doctorId = doctorId;
		this.patientNameSnapshot = patientNameSnapshot;

		this.appointmentDate = appointmentDate;
		this.startTime = startTime;
		this.slotDurationMinutes = slotDurationMinutes;
		this.bufferMinutes = bufferMinutes;

		this.endTime = startTime.plusMinutes(slotDurationMinutes);

// keep instant for history / analytics
		this.scheduledAt = ZonedDateTime.of(appointmentDate, startTime, clinicZoneId).toInstant();

		this.status = AppointmentStatus.CREATED;
	}

	public void confirm() {
		if (this.status == AppointmentStatus.CANCELLED) {
			throw new IllegalStateException("Cannot confirm a cancelled appointment");
		}
		this.status = AppointmentStatus.CONFIRMED;
	}

	public void reschedule(Instant newTime) {
		if (this.status == AppointmentStatus.CANCELLED) {
			throw new IllegalStateException("Cannot reschedule a cancelled appointment");
		}
		if (newTime == null) {
			throw new IllegalArgumentException("New time required");
		}
		this.scheduledAt = newTime;
	}

	public void cancel() {
		if (this.status == AppointmentStatus.CANCELLED) {
			return; // idempotent
		}
		this.status = AppointmentStatus.CANCELLED;
	}

	public boolean isActive() {
		return this.status != AppointmentStatus.CANCELLED;
	}
}
