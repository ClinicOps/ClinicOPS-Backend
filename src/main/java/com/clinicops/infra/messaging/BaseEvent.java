package com.clinicops.infra.messaging;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public abstract class BaseEvent {
    private final String eventId = UUID.randomUUID().toString();
    private final Instant occurredAt = Instant.now();
}
