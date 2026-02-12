package com.clinicops.modules.visit.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "visits")
@Data
@CompoundIndexes({
		@CompoundIndex(name = "workspace_patient_idx", def = "{'workspaceId':1, 'patientId':1, 'visitTime':-1}"),
		@CompoundIndex(name = "appointment_idx", def = "{'appointmentId':1}") })
public class Visit {

	@Id
	private String id;

	private String workspaceId;

	private String patientId;

	private String doctorId;

	private String appointmentId; // nullable

	private Instant visitTime;

	private String reason; // chief complaint / purpose

	private Instant createdAt;
}
