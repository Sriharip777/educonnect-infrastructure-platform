package com.tcon.api_gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
public class FallbackController {

    // ✅ FIX: @RequestMapping handles ALL HTTP methods (GET, POST, PUT, DELETE, PATCH)
    // Previously using @GetMapping caused 405 when circuit breaker forwarded POST requests

    @RequestMapping("/fallback/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        log.warn("⚠️ Auth service fallback triggered");
        return fallbackResponse("auth-user-service");
    }

    @RequestMapping("/fallback/user")
    public ResponseEntity<Map<String, Object>> userFallback() {
        log.warn("⚠️ User service fallback triggered");
        return fallbackResponse("auth-user-service");
    }

    @RequestMapping("/fallback/course")
    public ResponseEntity<Map<String, Object>> courseFallback() {
        log.warn("⚠️ Course service fallback triggered");
        return fallbackResponse("learning-management-service");
    }

    @RequestMapping("/fallback/booking")
    public ResponseEntity<Map<String, Object>> bookingFallback() {
        log.warn("⚠️ Booking service fallback triggered");
        return fallbackResponse("learning-management-service");
    }

    @RequestMapping("/fallback/demo")
    public ResponseEntity<Map<String, Object>> demoFallback() {
        log.warn("⚠️ Demo service fallback triggered");
        return fallbackResponse("learning-management-service");
    }

    @RequestMapping("/fallback/assignment")
    public ResponseEntity<Map<String, Object>> assignmentFallback() {
        log.warn("⚠️ Assignment service fallback triggered");
        return fallbackResponse("learning-management-service");
    }

    @RequestMapping("/fallback/question")
    public ResponseEntity<Map<String, Object>> questionFallback() {
        log.warn("⚠️ Question service fallback triggered");
        return fallbackResponse("learning-management-service");
    }

    @RequestMapping("/fallback/submission")
    public ResponseEntity<Map<String, Object>> submissionFallback() {
        log.warn("⚠️ Submission service fallback triggered");
        return fallbackResponse("learning-management-service");
    }

    @RequestMapping("/fallback/payment")
    public ResponseEntity<Map<String, Object>> paymentFallback() {
        log.warn("⚠️ Payment service fallback triggered");
        return fallbackResponse("financial-service");
    }

    @RequestMapping("/fallback/payout")
    public ResponseEntity<Map<String, Object>> payoutFallback() {
        log.warn("⚠️ Payout service fallback triggered");
        return fallbackResponse("financial-service");
    }

    @RequestMapping("/fallback/video")
    public ResponseEntity<Map<String, Object>> videoFallback() {
        log.warn("⚠️ Video service fallback triggered");
        return fallbackResponse("communication-service");
    }

    @RequestMapping("/fallback/message")
    public ResponseEntity<Map<String, Object>> messageFallback() {
        log.warn("⚠️ Messaging service fallback triggered");
        return fallbackResponse("communication-service");
    }

    @RequestMapping("/fallback/websocket")
    public ResponseEntity<Map<String, Object>> websocketFallback() {
        log.warn("⚠️ WebSocket service fallback triggered");
        return fallbackResponse("communication-service");
    }

    @RequestMapping("/fallback/whiteboard")
    public ResponseEntity<Map<String, Object>> whiteboardFallback() {
        log.warn("⚠️ Whiteboard service fallback triggered");
        return fallbackResponse("communication-service");
    }

    @RequestMapping("/fallback/notification")
    public ResponseEntity<Map<String, Object>> notificationFallback() {
        log.warn("⚠️ Notification service fallback triggered");
        return fallbackResponse("notification-service");
    }

    @RequestMapping("/fallback/integration")
    public ResponseEntity<Map<String, Object>> integrationFallback() {
        log.warn("⚠️ Integration service fallback triggered");
        return fallbackResponse("integration-service");
    }

    @RequestMapping("/fallback/content")
    public ResponseEntity<Map<String, Object>> contentFallback() {
        log.warn("⚠️ Content service fallback triggered");
        return fallbackResponse("content-service");
    }

    // ✅ Shared helper — keeps all fallbacks consistent
    private ResponseEntity<Map<String, Object>> fallbackResponse(String service) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "status", 503,
                "error", "Service Unavailable",
                "message", "Service is temporarily unavailable. Please try again later.",
                "service", service,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
