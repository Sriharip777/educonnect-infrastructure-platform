package com.tcon.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ===== Auth User Service (Port 8081) - Currently Running =====
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("http://localhost:8081"))

                .route("student-service", r -> r
                        .path("/api/student/**")
                        .uri("http://localhost:8081"))

                .route("teacher-service", r -> r
                        .path("/api/teacher/**")
                        .uri("http://localhost:8081"))

                .route("parent-service", r -> r
                        .path("/api/parent/**")
                        .uri("http://localhost:8081"))

                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .uri("http://localhost:8081"))

                // ===== Learning Management Service (Port 8082) - Future =====
                .route("course-service", r -> r
                        .path("/api/courses/**")
                        .uri("http://localhost:8082"))

                .route("booking-service", r -> r
                        .path("/api/bookings/**", "/api/sessions/**", "/api/availability/**")
                        .uri("http://localhost:8082"))

                .route("demo-service", r -> r
                        .path("/api/demo-classes/**")
                        .uri("http://localhost:8082"))

                // ===== Communication Service (Port 8083) - Future =====
                .route("video-service", r -> r
                        .path("/api/video/**")
                        .uri("http://localhost:8083"))

                .route("messaging-service", r -> r
                        .path("/api/messages/**", "/api/conversations/**")
                        .uri("http://localhost:8083"))

                // ===== Financial Service (Port 8084) - Future =====
                .route("payment-service", r -> r
                        .path("/api/payments/**", "/api/refunds/**")
                        .uri("http://localhost:8084"))

                .route("payout-service", r -> r
                        .path("/api/payouts/**", "/api/earnings/**")
                        .uri("http://localhost:8084"))

                // ===== Notification Service (Port 8085) - Future =====
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .uri("http://localhost:8085"))

                // ===== Integration Service (Port 8086) - Future =====
                .route("integration-service", r -> r
                        .path("/api/files/**", "/api/calendar/**", "/api/referrals/**", "/api/analytics/**")
                        .uri("http://localhost:8086"))

                // ===== Content Service (Port 8087) - Future =====
                .route("content-service", r -> r
                        .path("/api/recordings/**", "/api/reviews/**", "/api/materials/**", "/api/assignments/**")
                        .uri("http://localhost:8087"))

                .build();
    }
}
