package com.eventtracker.consumer.service;

import com.eventtracker.consumer.model.ActivityEvent;
import com.eventtracker.consumer.repository.ActivityEventRepository;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.Channel;

@Service
public class ActivityEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ActivityEventConsumer.class);
    private final ActivityEventRepository repository;

    public ActivityEventConsumer(ActivityEventRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name:user_activities}")
    public void listen(ActivityEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag)
            throws IOException {
        try {
            logger.info("Received event: {}", event);
            event.setProcessedAt(java.time.OffsetDateTime.now());
            repository.save(event);
            logger.info("Successfully processed and saved event for user:{}", event.getUserId());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("Error processing event for user:{}", event.getUserId(), e);
            channel.basicNack(tag, false, true);
        }
    }
}
