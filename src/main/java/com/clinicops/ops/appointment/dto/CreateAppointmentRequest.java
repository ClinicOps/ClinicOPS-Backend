package com.clinicops.ops.appointment.dto;

import java.time.Instant;

public record CreateAppointmentRequest(
	    String patientId,
	    Instant scheduledAt
	) {}

