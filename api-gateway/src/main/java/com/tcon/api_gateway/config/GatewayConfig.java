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
                // Auth & User Service Routes
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("lb://auth-user-service"))
                .route("user-service", r -> r
                        .path("/api/users/**", "/api/students/**", "/api/teachers/**", "/api/parents/**", "/api/admins/**")
                        .uri("lb://auth-user-service"))

                // Learning Management Service Routes
                .route("course-service", r -> r
                        .path("/api/courses/**")
                        .uri("lb://learning-management-service"))
                .route("booking-service", r -> r
                        .path("/api/bookings/**", "/api/sessions/**", "/api/availability/**")
                        .uri("lb://learning-management-service"))
                .route("demo-service", r -> r
                        .path("/api/demo-classes/**")
                        .uri("lb://learning-management-service"))

                // Communication Service Routes
                .route("video-service", r -> r
                        .path("/api/video/**")
                        .uri("lb://communication-service"))
                .route("messaging-service", r -> r
                        .path("/api/messages/**", "/api/conversations/**")
                        .uri("lb://communication-service"))

                // Financial Service Routes
                .route("payment-service", r -> r
                        .path("/api/payments/**", "/api/refunds/**")
                        .uri("lb://financial-service"))
                .route("payout-service", r -> r
                        .path("/api/payouts/**", "/api/earnings/**")
                        .uri("lb://financial-service"))

                // Notification Service Routes
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .uri("lb://notification-service"))

                // Integration Service Routes
                .route("integration-service", r -> r
                        .path("/api/files/**", "/api/calendar/**", "/api/referrals/**", "/api/analytics/**")
                        .uri("lb://integration-service"))

                // Content Service Routes
                .route("content-service", r -> r
                        .path("/api/recordings/**", "/api/reviews/**", "/api/materials/**", "/api/assignments/**")
                        .uri("lb://content-service"))

                .build();
    }
}
