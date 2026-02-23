package com.clinicops.ops.availability.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.clinicops.ops.appointment.model.Appointment;
import com.clinicops.ops.availability.model.DoctorAvailability;

public interface DoctorAvailabilityRepository extends MongoRepository<DoctorAvailability, ObjectId> {

	List<DoctorAvailability> findByClinicIdAndDoctorIdAndDayOfWeekAndIsActiveTrue(ObjectId clinicId, ObjectId doctorId,
			DayOfWeek dayOfWeek);

	@Query("""
			{
			  'clinicId': ?0,
			  'doctorId': ?1,
			  'dayOfWeek': ?2,
			  'isActive': true,
			  'startTime': { $lt: ?4 },
			  'endTime': { $gt: ?3 }
			}
			""")
	List<DoctorAvailability> findOverlappingAvailability(ObjectId clinicId, ObjectId doctorId, DayOfWeek dayOfWeek,
			LocalTime startTime, LocalTime endTime);

    
	
	@Query("""
			{
			  '_id': { $ne: ?5 },
			  'clinicId': ?0,
			  'doctorId': ?1,
			  'dayOfWeek': ?2,
			  'isActive': true,
			  'startTime': { $lt: ?4 },
			  'endTime': { $gt: ?3 }
			}
			""")
			List<DoctorAvailability> findOverlappingAvailabilityExcludingId(
			        ObjectId clinicId,
			        ObjectId doctorId,
			        DayOfWeek dayOfWeek,
			        LocalTime startTime,
			        LocalTime endTime,
			        ObjectId excludeId
			);
}
