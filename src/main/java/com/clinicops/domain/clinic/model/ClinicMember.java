package com.clinicops.domain.clinic.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.clinicops.common.model.BaseEntity;

import lombok.Data;

@Document(collection = "clinic_memberships")
@CompoundIndexes({ @CompoundIndex(name = "membership_user_idx", def = "{'userId':1}"),
		@CompoundIndex(name = "membership_clinic_idx", def = "{'clinicId':1}"),
		@CompoundIndex(name = "membership_unique", def = "{'userId':1, 'clinicId':1}", unique = true) })
@Data
public class ClinicMember extends BaseEntity {

	private ObjectId userId;

	private ObjectId organizationId;

	private ObjectId clinicId;

	private ClinicRole role;

	private MembershipStatus status;

	private boolean deleted;
}
