package com.clinicops.ops.appointment.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.clinicops.ops.appointment.model.Appointment;
import com.clinicops.ops.appointment.model.AppointmentStatus;

public interface AppointmentRepository extends MongoRepository<Appointment, ObjectId> {

	List<Appointment> findByClinicId(ObjectId clinicId);

	Page<Appointment> findByClinicIdAndStatusNot(ObjectId clinicId, AppointmentStatus status, Pageable pageable);

	List<Appointment> findByClinicIdAndDoctorIdAndAppointmentDate(ObjectId clinicId, ObjectId doctorId, LocalDate date);

	@Query("""
			{
			  'clinicId': ?0,
			  'doctorId': ?1,
			  'date': { $gte: ?2 },
			  'startTime': { $gte: ?3, $lt: ?4 }
			}
			""")
	List<Appointment> findFutureAppointmentsInTimeRange(ObjectId clinicId, ObjectId doctorId, LocalDate fromDate,
			LocalTime startTime, LocalTime endTime);

	boolean existsByClinicIdAndDoctorIdAndAppointmentDateGreaterThanEqual(ObjectId clinicId, ObjectId doctorId,
			LocalDate today);

	boolean existsByClinicIdAndDoctorIdAndAppointmentDateAndStartTime(ObjectId clinicId, ObjectId doctorId,
			LocalDate date, LocalTime startTime);

	List<Appointment> findByClinicIdAndDoctorIdAndAppointmentDateAndStartTimeBetween(ObjectId clinicId,
			ObjectId doctorId, LocalDate date, LocalTime start, LocalTime end);
}
