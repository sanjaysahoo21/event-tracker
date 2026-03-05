package com.eventtracker.consumer.service;

import com.eventtracker.consumer.model.ActivityEvent;
import com.eventtracker.consumer.repository.ActivityEventRepository;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActivityEventConsumerTest {

    private ActivityEventRepository repository;
    private Channel channel;
    private ActivityEventConsumer consumer;

    @BeforeEach
    void setUp() {
        repository = mock(ActivityEventRepository.class);
        channel = mock(Channel.class);
        consumer = new ActivityEventConsumer(repository);
    }

    @Test
    void listen_SuccessMessage_ShouldSaveAndAck() throws IOException {
        // Arrange
        ActivityEvent event = new ActivityEvent(
                "user-1", "user_login", OffsetDateTime.now(), Map.of("ip", "1.1.1.1"));
        long deliveryTag = 123L;

        // Act
        consumer.listen(event, channel, deliveryTag);

        // Assert
        ArgumentCaptor<ActivityEvent> eventCaptor = ArgumentCaptor.forClass(ActivityEvent.class);
        verify(repository, times(1)).save(eventCaptor.capture());

        ActivityEvent savedEvent = eventCaptor.getValue();
        assertNotNull(savedEvent.getProcessedAt(), "processedAt should be set on the event");
        assertEquals("user-1", savedEvent.getUserId());

        verify(channel, times(1)).basicAck(deliveryTag, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void listen_ExceptionThrown_ShouldNackAndRequeue() throws IOException {
        // Arrange
        ActivityEvent event = new ActivityEvent("user-2", "user_click", OffsetDateTime.now(), Map.of());
        long deliveryTag = 456L;

        // Simulate DB failure
        doThrow(new RuntimeException("Database error")).when(repository).save(any(ActivityEvent.class));

        // Act
        consumer.listen(event, channel, deliveryTag);

        // Assert
        verify(repository, times(1)).save(any(ActivityEvent.class));
        verify(channel, times(1)).basicNack(deliveryTag, false, true); // Must requeue
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }
}
