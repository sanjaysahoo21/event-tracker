package com.eventtracker.api.interceptor;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final long windowMs;
    private final int maxRequests;
    private final ConcurrentHashMap<String, RequestData> clientRequests = new ConcurrentHashMap<>();

    public RateLimitInterceptor(@Value("${rate.limit.window-ms:60000}") long windowMs,
            @Value("${rate.limit.max-requests:50}") int maxRequests) {
        this.windowMs = windowMs;
        this.maxRequests = maxRequests;
    }

    private static class RequestData {
        long startTime;
        int count;

        RequestData(int count, long startTime) {
            this.startTime = startTime;
            this.count = count;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String ip = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        clientRequests.putIfAbsent(ip, new RequestData(0, currentTime));
        RequestData data = clientRequests.get(ip);

        synchronized (data) {

            if (currentTime - data.startTime > windowMs) {
                data.startTime = currentTime;
                data.count = 0;
            }

            if (data.count >= maxRequests) {
                long timeElapsed = currentTime - data.startTime;
                long msRemaining = windowMs - timeElapsed;
                long secondsRemaining = msRemaining / 1000;

                response.setHeader("Retry-After", String.valueOf(secondsRemaining));
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many requests. Please try again later.");
                return false;
            }

            data.count++;
            return true;
        }
    }
}
