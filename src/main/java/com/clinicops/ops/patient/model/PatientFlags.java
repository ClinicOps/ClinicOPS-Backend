package com.clinicops.ops.patient.model;

import lombok.Getter;

@Getter
public class PatientFlags {

	private boolean vip;
	private boolean blacklisted;

	protected PatientFlags() {
	}

	public PatientFlags(boolean vip, boolean blacklisted) {
		this.vip = vip;
		this.blacklisted = blacklisted;
	}
}
