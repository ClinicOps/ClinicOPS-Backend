package com.clinicops.domain.ops.dto;

import java.time.Instant;

public record CreateAppointmentRequest(
	    String patientId,
	    Instant scheduledAt
	) {}

