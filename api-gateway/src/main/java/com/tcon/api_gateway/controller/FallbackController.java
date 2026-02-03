package com.tcon.api_gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        log.warn("⚠️ Auth service fallback triggered");
        return buildFallbackResponse("Auth User Service is temporarily unavailable");
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> userFallback() {
        log.warn("⚠️ User service fallback triggered");
        return buildFallbackResponse("User Service is temporarily unavailable");
    }

    @GetMapping("/course")
    public ResponseEntity<Map<String, Object>> courseFallback() {
        log.warn("⚠️ Course service fallback triggered");
        return buildFallbackResponse("Course Service is temporarily unavailable");
    }

    @GetMapping("/booking")
    public ResponseEntity<Map<String, Object>> bookingFallback() {
        log.warn("⚠️ Booking service fallback triggered");
        return buildFallbackResponse("Booking Service is temporarily unavailable");
    }

    @GetMapping("/payment")
    public ResponseEntity<Map<String, Object>> paymentFallback() {
        log.warn("⚠️ Payment service fallback triggered");
        return buildFallbackResponse("Payment Service is temporarily unavailable");
    }

    @GetMapping("/video")
    public ResponseEntity<Map<String, Object>> videoFallback() {
        log.warn("⚠️ Video service fallback triggered");
        return buildFallbackResponse("Video Service is temporarily unavailable");
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationFallback() {
        log.warn("⚠️ Notification service fallback triggered");
        return buildFallbackResponse("Notification Service is temporarily unavailable");
    }

    @GetMapping("/integration")
    public ResponseEntity<Map<String, Object>> integrationFallback() {
        log.warn("⚠️ Integration service fallback triggered");
        return buildFallbackResponse("Integration Service is temporarily unavailable");
    }

    @GetMapping("/content")
    public ResponseEntity<Map<String, Object>> contentFallback() {
        log.warn("⚠️ Content service fallback triggered");
        return buildFallbackResponse("Content Service is temporarily unavailable");
    }

    private ResponseEntity<Map<String, Object>> buildFallbackResponse(String message) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "error", "Service Unavailable",
                        "message", message,
                        "note", "Please try again later"
                )
        );
    }
}
