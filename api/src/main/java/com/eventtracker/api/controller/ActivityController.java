package com.eventtracker.api.controller;

import com.eventtracker.api.dto.ActivityEventDto;
import com.eventtracker.api.service.ActivityEventProducer;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    private final ActivityEventProducer activityEventProducer;

    public ActivityController(ActivityEventProducer activityEventProducer) {
        this.activityEventProducer = activityEventProducer;
    }

    @PostMapping
    public ResponseEntity<Void> sendActivityEvent(@Valid @RequestBody ActivityEventDto event) {
        activityEventProducer.sendActivityEvent(event);
        return ResponseEntity.accepted().build();
    }
}
