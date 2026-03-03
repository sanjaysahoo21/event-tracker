package com.eventtracker.api.dto;

import java.time.OffsetDateTime;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ActivityEventDto {
    @NotBlank(message = "User ID is required")
    private String userId;
    @NotBlank(message = "Event type is required")
    private String eventType;
    @NotNull(message = "Timestamp is required")
    private OffsetDateTime timestamp;
    @NotNull(message = "Payload is required")
    private Map<String, Object> payload;

    public ActivityEventDto() {
    }

    public ActivityEventDto(String userId, String eventType, OffsetDateTime timestamp, Map<String, Object> payload) {
        this.userId = userId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "ActivityEventDto{" +
                "userId='" + userId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", payload=" + payload +
                '}';
    }
}
