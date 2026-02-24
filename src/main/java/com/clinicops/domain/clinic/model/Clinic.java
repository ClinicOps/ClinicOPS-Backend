package com.clinicops.domain.clinic.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

import lombok.Data;

@Document(collection = "clinics")
@CompoundIndexes({ @CompoundIndex(name = "clinic_code_unique", def = "{'code':1}", unique = true),
		@CompoundIndex(name = "clinic_org_idx", def = "{'organizationId':1}") })
@Data
public class Clinic extends BaseEntity {

	private ObjectId organizationId;

	private String name;
	private String code;

	private String timezone;

	private String email;
	private String phone;

	private Address address;

	private ClinicStatus status;

	private boolean deleted;
}