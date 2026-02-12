package com.clinicops.config;



import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitNotificationConfig {

    public static final String NOTIFICATION_QUEUE =
            "appointment.notifications";

    @Bean
    Queue appointmentNotificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    Binding appointmentNotificationBinding(
            Queue appointmentNotificationQueue,
            TopicExchange domainEventsExchange) {

        return BindingBuilder
            .bind(appointmentNotificationQueue)
            .to(domainEventsExchange)
            .with("appointment.*");
    }
}
