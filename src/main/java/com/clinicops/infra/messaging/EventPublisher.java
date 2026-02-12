package com.clinicops.infra.messaging;

public interface EventPublisher {
    void publish(BaseEvent event);
}
