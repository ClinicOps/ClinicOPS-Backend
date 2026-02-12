package com.clinicops.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitConfig {

    public static final String DOMAIN_EVENTS_EXCHANGE = "clinicops.domain.events";

    @Bean
    public TopicExchange domainEventsExchange() {
        return new TopicExchange(DOMAIN_EVENTS_EXCHANGE);
    }

    @SuppressWarnings({ "removal", "deprecation" })
	@Bean
    Jackson2JsonMessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @SuppressWarnings("removal")
	@Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
