package com.tcon.api_gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.time.Duration;

@Slf4j
@Configuration
public class GatewayConfig {

    private final KeyResolver userKeyResolver;
    private final RedisRateLimiter authRateLimiter;
    private final RedisRateLimiter paymentRateLimiter;

    private static final HttpMethod[] ALL_METHODS = {
            HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
            HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.OPTIONS
    };

    public GatewayConfig(
            KeyResolver userKeyResolver,
            @Qualifier("authRateLimiter") RedisRateLimiter authRateLimiter,
            @Qualifier("paymentRateLimiter") RedisRateLimiter paymentRateLimiter) {
        this.userKeyResolver = userKeyResolver;
        this.authRateLimiter = authRateLimiter;
        this.paymentRateLimiter = paymentRateLimiter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("🚀 Configuring API Gateway Routes");

        return builder.routes()

                // ===== AUTH SERVICE =====
                .route("auth-password-reset", r -> r
                        .path("/api/auth/password/reset-request", "/api/auth/password/reset")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/auth"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://auth-user-service"))

                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(authRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setDenyEmptyKey(false))
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/auth"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, false)))
                        .uri("lb://auth-user-service"))

                // ===== WORKSHEET SERVICE =====
                .route("worksheet-admin-service", r -> r
                        .path("/api/admin/worksheets/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/course"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                .route("student-worksheet-service", r -> r
                        .path("/api/student/worksheets/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/course"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                // ===== USER ROLE SERVICES =====
                .route("student-service", r -> r
                        .path("/api/student/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://auth-user-service"))

                .route("teacher-worksheet-service", r -> r
                        .path("/api/teacher/worksheets/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/course"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                .route("teacher-earnings-service", r -> r
                        .path(
                                "/api/teacher/earnings",
                                "/api/teacher/earnings/**",
                                "/api/teacher/analytics",
                                "/api/teacher/analytics/**"
                        )
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("payment-service-cb")
                                        .setFallbackUri("forward:/fallback/payout"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://financial-service"))

                .route("teacher-service", r -> r
                        .path("/api/teacher/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://auth-user-service"))

                .route("parent-service", r -> r
                        .path("/api/parent/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://auth-user-service"))

                .route("admin-earnings-service", r -> r
                        .path(
                                "/api/admin/earnings",
                                "/api/admin/earnings/**"
                        )
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("payment-service-cb")
                                        .setFallbackUri("forward:/fallback/payout"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://financial-service"))

                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://auth-user-service"))

                .route("user-service", r -> r
                        .path("/user-service/api/users/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://auth-user-service"))

                // ===== WORKSHEET PUBLIC SERVICE =====
                .route("worksheet-public-service", r -> r
                        .path("/api/worksheets/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/course"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                // ===== LEARNING MANAGEMENT SERVICE =====
                .route("course-service", r -> r
                        .path("/api/courses/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/course"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                .route("booking-service", r -> r
                        .path(
                                "/api/bookings",
                                "/api/bookings/**",
                                "/api/sessions",
                                "/api/sessions/**",
                                "/api/availability",
                                "/api/availability/**"
                        )
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/booking"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                .route("demo-service", r -> r
                        .path("/api/demos/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/demo"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                .route("grades-subjects-topics", r -> r
                        .path("/api/grades/**", "/api/subjects/**", "/api/topics/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/course"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                .route("curriculum-service", r -> r
                        .path("/api/curriculum/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/course"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                .route("assignment-service", r -> r
                        .path("/api/assignments/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/assignment"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                .route("question-service", r -> r
                        .path("/api/questions/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/question"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                .route("submission-service", r -> r
                        .path("/api/submissions/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/submission"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                .route("resource-service", r -> r
                        .path("/api/resources/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("lms-cb")
                                        .setFallbackUri("forward:/fallback/course"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://learning-management-service"))

                // ===== COMMUNICATION SERVICE =====
                .route("communication-ws", r -> r
                        .path("/ws-messaging/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("video-service-cb")
                                        .setFallbackUri("forward:/fallback/websocket")))
                        .uri("lb:ws://communication-service"))

                .route("whiteboard-service", r -> r
                        .path("/api/whiteboard/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("video-service-cb")
                                        .setFallbackUri("forward:/fallback/whiteboard"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, false)))
                        .uri("lb://communication-service"))

                .route("video-service", r -> r
                        .path("/api/video/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("video-service-cb")
                                        .setFallbackUri("forward:/fallback/video"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://communication-service"))

                .route("messaging-service", r -> r
                        .path("/api/messages/**", "/api/conversations/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("messaging-service-cb")
                                        .setFallbackUri("forward:/fallback/message"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://communication-service"))

                // ===== FINANCIAL SERVICE =====
                .route("payment-service", r -> r
                        .path(
                                "/api/payments",
                                "/api/payments/**",
                                "/api/refunds",
                                "/api/refunds/**"
                        )
                        .filters(f -> f
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(paymentRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setDenyEmptyKey(false))
                                .circuitBreaker(c -> c
                                        .setName("payment-service-cb")
                                        .setFallbackUri("forward:/fallback/payment"))
                                .retry(config -> config
                                        .setRetries(3)
                                        .setMethods(ALL_METHODS)
                                        .setBackoff(Duration.ofMillis(200), Duration.ofMillis(2000), 2, false)))
                        .uri("lb://financial-service"))

                .route("payout-service", r -> r
                        .path(
                                "/api/payouts",
                                "/api/payouts/**",
                                "/api/earnings",
                                "/api/earnings/**"
                        )
                        .filters(f -> f
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(paymentRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setDenyEmptyKey(false))
                                .circuitBreaker(c -> c
                                        .setName("payment-service-cb")
                                        .setFallbackUri("forward:/fallback/payout"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://financial-service"))

                // ===== NOTIFICATION SERVICE =====
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("notification-service-cb")
                                        .setFallbackUri("forward:/fallback/notification"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://notification-service"))

                // ===== INTEGRATION SERVICE =====
                .route("integration-service", r -> r
                        .path("/api/files/**", "/api/calendar/**", "/api/referrals/**", "/api/analytics/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("integration-service-cb")
                                        .setFallbackUri("forward:/fallback/integration"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://integration-service"))

                // ===== CONTENT SERVICE =====
                .route("content-service", r -> r
                        .path("/api/recordings/**", "/api/reviews/**", "/api/materials/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("content-service-cb")
                                        .setFallbackUri("forward:/fallback/content"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://content-service"))

                .route("actuator", r -> r
                        .path("/actuator/**")
                        .filters(f -> f
                                .retry(config -> config
                                        .setRetries(1)
                                        .setMethods(HttpMethod.GET)))
                        .uri("lb://auth-user-service"))

                .route("customer-support-service", r -> r
                        .path("/api/support/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("support-service-cb"))
                                .retry(config -> config
                                        .setRetries(2)
                                        .setMethods(ALL_METHODS)))
                        .uri("lb://customer-support-service"))

                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> lmsCircuitBreakerCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .slowCallDurationThreshold(Duration.ofSeconds(5))
                        .slowCallRateThreshold(50.0f)
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(10))
                        .cancelRunningFuture(true)
                        .build())
                .build(), "lms-cb");
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> authCircuitBreakerCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(5))
                        .cancelRunningFuture(true)
                        .build())
                .build(), "auth-service-cb");
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> paymentCircuitBreakerCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(20)
                        .minimumNumberOfCalls(10)
                        .failureRateThreshold(40.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(60))
                        .slowCallDurationThreshold(Duration.ofSeconds(3))
                        .slowCallRateThreshold(40.0f)
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(15))
                        .cancelRunningFuture(true)
                        .build())
                .build(), "payment-service-cb");
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> videoCircuitBreakerCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(15)
                        .minimumNumberOfCalls(8)
                        .failureRateThreshold(60.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(45))
                        .slowCallDurationThreshold(Duration.ofSeconds(5))
                        .slowCallRateThreshold(60.0f)
                        .permittedNumberOfCallsInHalfOpenState(4)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(20))
                        .cancelRunningFuture(true)
                        .build())
                .build(), "video-service-cb");
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> messagingCircuitBreakerCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .slowCallDurationThreshold(Duration.ofSeconds(3))
                        .slowCallRateThreshold(50.0f)
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(10))
                        .cancelRunningFuture(true)
                        .build())
                .build(), "messaging-service-cb");
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> notificationCircuitBreakerCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(20))
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(5))
                        .cancelRunningFuture(true)
                        .build())
                .build(), "notification-service-cb");
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> integrationCircuitBreakerCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .slowCallDurationThreshold(Duration.ofSeconds(5))
                        .slowCallRateThreshold(50.0f)
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(15))
                        .cancelRunningFuture(true)
                        .build())
                .build(), "integration-service-cb");
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> contentCircuitBreakerCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .slowCallDurationThreshold(Duration.ofSeconds(5))
                        .slowCallRateThreshold(50.0f)
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(15))
                        .cancelRunningFuture(true)
                        .build())
                .build(), "content-service-cb");
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> supportCircuitBreakerCustomizer() {
        return factory -> factory.configure(builder -> builder
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(20)
                        .minimumNumberOfCalls(10)
                        .failureRateThreshold(60.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(15))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(30))
                        .cancelRunningFuture(true)
                        .build())
                .build(), "support-service-cb");
    }
}