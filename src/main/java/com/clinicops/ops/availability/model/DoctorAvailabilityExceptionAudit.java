package com.clinicops.ops.availability.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Document("doctor_availability_exception_audit")
@Getter
@Setter
public class DoctorAvailabilityExceptionAudit extends BaseEntity {

	private ObjectId exceptionId;
	private ObjectId clinicId;
	private ObjectId doctorId;

	private String action; // CREATE, UPDATE, DELETE
	private ObjectId performedBy;

	private String summary;
}
