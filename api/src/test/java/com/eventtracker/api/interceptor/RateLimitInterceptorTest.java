package com.eventtracker.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RateLimitInterceptorTest {

    private RateLimitInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private final Object handler = new Object();

    @BeforeEach
    void setUp() throws Exception {
        // Use limit of 3 requests per window to keep tests fast
        interceptor = new RateLimitInterceptor(60000, 3);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));
    }

    @Test
    void preHandle_UnderLimit_ShouldAllowRequest() throws Exception {
        assertTrue(interceptor.preHandle(request, response, handler));
        assertTrue(interceptor.preHandle(request, response, handler));
        assertTrue(interceptor.preHandle(request, response, handler));
        verify(response, never()).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    @Test
    void preHandle_ExceedsLimit_ShouldReturn429WithRetryAfterHeader() throws Exception {
        // Exhaust the 3-request limit
        for (int i = 0; i < 3; i++) {
            interceptor.preHandle(request, response, handler);
        }

        boolean result = interceptor.preHandle(request, response, handler);

        assertFalse(result);
        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(response).setHeader(eq("Retry-After"), anyString());
    }

    @Test
    void preHandle_DifferentIps_ShouldTrackSeparately() throws Exception {
        HttpServletRequest otherRequest = mock(HttpServletRequest.class);
        when(otherRequest.getRemoteAddr()).thenReturn("10.0.0.2");

        // Exhaust limit for first IP
        for (int i = 0; i < 3; i++) {
            interceptor.preHandle(request, response, handler);
        }
        assertFalse(interceptor.preHandle(request, response, handler));

        // A different IP should still be allowed
        assertTrue(interceptor.preHandle(otherRequest, response, handler));
    }
}
