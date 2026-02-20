package com.clinicops.domain.ops.model;

import lombok.Getter;

import java.util.List;

@Getter
public class PatientMedical {

	private List<String> allergies;
	private List<String> chronicConditions;
	private String notes;

	protected PatientMedical() {
	}

	public PatientMedical(List<String> allergies, List<String> chronicConditions, String notes) {

		this.allergies = allergies;
		this.chronicConditions = chronicConditions;
		this.notes = notes;
	}
}
