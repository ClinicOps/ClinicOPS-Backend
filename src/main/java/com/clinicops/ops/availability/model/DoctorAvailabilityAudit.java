package com.clinicops.ops.availability.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Document("doctor_availability_audit")
@Getter
@Setter
@CompoundIndex(name = "clinic_doctor_audit_idx", def = "{'clinicId':1,'doctorId':1,'createdAt':-1}")
public class DoctorAvailabilityAudit extends BaseEntity {

	private ObjectId availabilityId;
	private ObjectId clinicId;
	private ObjectId doctorId;

	private String action; // CREATE, UPDATE, DEACTIVATE
	private ObjectId performedBy;

	private String summary;
}
