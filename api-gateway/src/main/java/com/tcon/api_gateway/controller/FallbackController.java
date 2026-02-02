package com.tcon.api_gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        log.warn("⚠️ Auth service fallback triggered - service unavailable");
        return buildFallbackResponse("Auth Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> userFallback() {
        log.warn("⚠️ User service fallback triggered - service unavailable");
        return buildFallbackResponse("User Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/course")
    public ResponseEntity<Map<String, Object>> courseFallback() {
        log.warn("⚠️ Course service fallback triggered - service unavailable");
        return buildFallbackResponse("Course Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/booking")
    public ResponseEntity<Map<String, Object>> bookingFallback() {
        log.warn("⚠️ Booking service fallback triggered - service unavailable");
        return buildFallbackResponse("Booking Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> demoFallback() {
        log.warn("⚠️ Demo service fallback triggered - service unavailable");
        return buildFallbackResponse("Demo Class Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/video")
    public ResponseEntity<Map<String, Object>> videoFallback() {
        log.warn("⚠️ Video service fallback triggered - service unavailable");
        return buildFallbackResponse("Video Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/message")
    public ResponseEntity<Map<String, Object>> messageFallback() {
        log.warn("⚠️ Messaging service fallback triggered - service unavailable");
        return buildFallbackResponse("Messaging Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/payment")
    public ResponseEntity<Map<String, Object>> paymentFallback() {
        log.warn("⚠️ Payment service fallback triggered - service unavailable");
        return buildFallbackResponse("Payment Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/payout")
    public ResponseEntity<Map<String, Object>> payoutFallback() {
        log.warn("⚠️ Payout service fallback triggered - service unavailable");
        return buildFallbackResponse("Payout Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationFallback() {
        log.warn("⚠️ Notification service fallback triggered - service unavailable");
        return buildFallbackResponse("Notification Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/integration")
    public ResponseEntity<Map<String, Object>> integrationFallback() {
        log.warn("⚠️ Integration service fallback triggered - service unavailable");
        return buildFallbackResponse("Integration Service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/content")
    public ResponseEntity<Map<String, Object>> contentFallback() {
        log.warn("⚠️ Content service fallback triggered - service unavailable");
        return buildFallbackResponse("Content Service is temporarily unavailable. Please try again later.");
    }

    private ResponseEntity<Map<String, Object>> buildFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        response.put("error", "SERVICE_UNAVAILABLE");

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
