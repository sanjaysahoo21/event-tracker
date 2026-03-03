package com.eventtracker.api.service;

import com.eventtracker.api.dto.ActivityEventDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ActivityEventProducer {
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

        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
