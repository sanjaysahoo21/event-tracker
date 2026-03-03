package com.eventtracker.consumer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.OffsetDateTime;
import java.util.Map;

@Document(collection = "activities")
public class ActivityEvent {
    @Id
    private String id;
    private String userId;
    private String eventType;
    private OffsetDateTime timestamp;
    private Map<String, Object> payload;
    private OffsetDateTime processedAt;

    public ActivityEvent() {
    }

    public ActivityEvent(String userId, String eventType, OffsetDateTime timestamp, Map<String, Object> payload) {
        this.userId = userId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public OffsetDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(OffsetDateTime processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public String toString() {
        return "ActivityEvent{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", payload=" + payload +
                ", processedAt=" + processedAt +
                '}';
    }
}
