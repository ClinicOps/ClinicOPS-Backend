package com.clinicops.ops.patient.model;

import com.clinicops.common.model.BaseEntity;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document("patients")
@CompoundIndexes({
		@CompoundIndex(name = "clinic_mobile_unique", def = "{'clinicId': 1, 'contact.mobile': 1}", unique = true),
		@CompoundIndex(name = "clinic_patientCode_unique", def = "{'clinicId': 1, 'patientCode': 1}", unique = true),
		@CompoundIndex(name = "clinic_status_idx", def = "{'clinicId': 1, 'status': 1}") })
public class Patient extends BaseEntity {

	private ObjectId clinicId;
	private String patientCode;

	private PatientPersonal personal;
	private PatientContact contact;
	private PatientMedical medical;
	private PatientFlags flags;

	private PatientStatus status;

	protected Patient() {
	}

	public Patient(ObjectId clinicId, String patientCode, PatientPersonal personal, PatientContact contact,
			PatientMedical medical) {

		if (clinicId == null) {
			throw new IllegalArgumentException("ClinicId required");
		}

		if (patientCode == null || patientCode.isBlank()) {
			throw new IllegalArgumentException("Patient code required");
		}

		this.clinicId = clinicId;
		this.patientCode = patientCode;
		this.personal = personal;
		this.contact = contact;
		this.medical = medical;
		this.flags = new PatientFlags(false, false);
		this.status = PatientStatus.ACTIVE;
	}

	public void archive() {
		if (this.status == PatientStatus.ARCHIVED)
			return;
		this.status = PatientStatus.ARCHIVED;
	}

	public void activate() {
		if (this.status == PatientStatus.ACTIVE)
			return;
		this.status = PatientStatus.ACTIVE;
	}

	public void updateContact(PatientContact contact) {
		this.contact = contact;
	}

	public void updateMedical(PatientMedical medical) {
		this.medical = medical;
	}

}
