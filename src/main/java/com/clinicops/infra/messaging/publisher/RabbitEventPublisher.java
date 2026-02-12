package com.clinicops.infra.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.clinicops.config.RabbitConfig;
import com.clinicops.infra.messaging.BaseEvent;
import com.clinicops.infra.messaging.EventPublisher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RabbitEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(BaseEvent event) {

        String routingKey = resolveRoutingKey(event);

        rabbitTemplate.convertAndSend(
            RabbitConfig.DOMAIN_EVENTS_EXCHANGE,
            routingKey,
            event
        );
    }

    private String resolveRoutingKey(BaseEvent event) {

        // Example:
        // AppointmentBookedEvent -> appointment.booked
        String simpleName = event.getClass().getSimpleName();

        return simpleName
                .replace("Event", "")
                .replaceAll("([a-z])([A-Z])", "$1.$2")
                .toLowerCase();
    }
}

