package com.eventtracker.api.controller;

import com.eventtracker.api.dto.ActivityEventDto;
import com.eventtracker.api.service.ActivityEventProducer;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eventtracker.api.interceptor.RateLimitInterceptor;

import org.junit.jupiter.api.BeforeEach;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(ActivityController.class)
public class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityEventProducer activityEventProducer;

    @MockitoBean
    private RateLimitInterceptor rateLimitInterceptor;

    @BeforeEach
    public void setup() throws Exception {
        when(rateLimitInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any()))
                .thenReturn(true);
    }

    @Test
    public void testValidActivityEvent() throws Exception {
        String validJson = """
                {
                    "userId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                    "eventType": "user_login",
                    "timestamp": "2023-10-27T10:00:00Z",
                    "payload": {
                        "ipAddress": "192.168.1.1",
                        "device": "desktop",
                        "browser": "Chrome"
                    }
                }
                """;

        mockMvc.perform(post("/api/v1/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andExpect(status().isAccepted());

        verify(activityEventProducer).sendActivityEvent(any(ActivityEventDto.class));
    }

    @Test
    public void testInvalidActivityEvent_MissingUserId() throws Exception {
        String invalidJson = """
                {
                    "eventType": "user_login",
                    "timestamp": "2023-10-27T10:00:00Z",
                    "payload": {
                        "ipAddress": "192.168.1.1"
                    }
                }
                """;

        mockMvc.perform(post("/api/v1/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
