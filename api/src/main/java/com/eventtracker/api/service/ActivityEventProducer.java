package com.eventtracker.api.service;

import com.eventtracker.api.dto.ActivityEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ActivityEventProducer {
    private static final Logger logger = LoggerFactory.getLogger(ActivityEventProducer.class);
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public ActivityEventProducer(RabbitTemplate rabbitTemplate,
            @Value("${spring.rabbitmq.template.exchange}") String exchange,
            @Value("${spring.rabbitmq.template.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void sendActivityEvent(ActivityEventDto event) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            logger.info("Published activity event for user: {}", event.getUserId());
        } catch (AmqpException e) {
            logger.error("Failed to publish activity event for user: {}", event.getUserId(), e);
            throw e;
        }
    }
}
