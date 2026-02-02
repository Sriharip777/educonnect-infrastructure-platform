package com.tcon.api_gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ===== Auth User Service - WITH LOAD BALANCING =====
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/auth"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, false)))
                        .uri("lb://auth-user-service"))  // 🔥 Load Balanced

                .route("student-service", r -> r
                        .path("/api/student/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("student-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://auth-user-service"))  // 🔥 Load Balanced

                .route("teacher-service", r -> r
                        .path("/api/teacher/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("teacher-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://auth-user-service"))  // 🔥 Load Balanced

                .route("parent-service", r -> r
                        .path("/api/parent/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("parent-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://auth-user-service"))  // 🔥 Load Balanced

                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("admin-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://auth-user-service"))  // 🔥 Load Balanced

                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("user-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://auth-user-service"))  // 🔥 Load Balanced

                // ===== Learning Management Service - WITH LOAD BALANCING =====
                .route("course-service", r -> r
                        .path("/api/courses/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("course-service-cb")
                                        .setFallbackUri("forward:/fallback/course"))
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://learning-management-service"))  // 🔥 Load Balanced

                .route("booking-service", r -> r
                        .path("/api/bookings/**", "/api/sessions/**", "/api/availability/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("booking-service-cb")
                                        .setFallbackUri("forward:/fallback/booking"))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter()))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://learning-management-service"))  // 🔥 Load Balanced

                .route("demo-service", r -> r
                        .path("/api/demos/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("demo-service-cb")
                                        .setFallbackUri("forward:/fallback/demo"))
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://learning-management-service"))  // 🔥 Load Balanced

                // ===== Communication Service - WITH LOAD BALANCING =====
                .route("video-service", r -> r
                        .path("/api/video/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("video-service-cb")
                                        .setFallbackUri("forward:/fallback/video"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://communication-service"))  // 🔥 Load Balanced

                .route("messaging-service", r -> r
                        .path("/api/messages/**", "/api/conversations/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("messaging-service-cb")
                                        .setFallbackUri("forward:/fallback/message"))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter()))
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://communication-service"))  // 🔥 Load Balanced

                // ===== Financial Service - WITH LOAD BALANCING =====
                .route("payment-service", r -> r
                        .path("/api/payments/**", "/api/refunds/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("payment-service-cb")
                                        .setFallbackUri("forward:/fallback/payment"))
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter()))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://financial-service"))  // 🔥 Load Balanced

                .route("payout-service", r -> r
                        .path("/api/payouts/**", "/api/earnings/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("payout-service-cb")
                                        .setFallbackUri("forward:/fallback/payout"))
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://financial-service"))  // 🔥 Load Balanced

                // ===== Notification Service - WITH LOAD BALANCING =====
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("notification-service-cb")
                                        .setFallbackUri("forward:/fallback/notification"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://notification-service"))  // 🔥 Load Balanced

                // ===== Integration Service - WITH LOAD BALANCING =====
                .route("integration-service", r -> r
                        .path("/api/files/**", "/api/calendar/**", "/api/referrals/**", "/api/analytics/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("integration-service-cb")
                                        .setFallbackUri("forward:/fallback/integration"))
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://integration-service"))  // 🔥 Load Balanced

                // ===== Content Service - WITH LOAD BALANCING =====
                .route("content-service", r -> r
                        .path("/api/recordings/**", "/api/reviews/**", "/api/materials/**", "/api/assignments/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("content-service-cb")
                                        .setFallbackUri("forward:/fallback/content"))
                                .retry(config -> config.setRetries(3)))
                        .uri("lb://content-service"))  // 🔥 Load Balanced

                .build();
    }

    /**
     * Redis Rate Limiter
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(100, 200); // replenishRate, burstCapacity
    }

    /**
     * Circuit Breaker Configuration
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .slowCallDurationThreshold(Duration.ofSeconds(2))
                        .slowCallRateThreshold(50.0f)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(5))
                        .build())
                .build());
    }
}
