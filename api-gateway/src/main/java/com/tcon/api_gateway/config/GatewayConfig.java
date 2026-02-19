package com.tcon.api_gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final KeyResolver userKeyResolver;

    @Qualifier("authRateLimiter")
    private final RedisRateLimiter authRateLimiter;

    @Qualifier("paymentRateLimiter")
    private final RedisRateLimiter paymentRateLimiter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("🚀 Configuring API Gateway Routes with Circuit Breakers, Rate Limiting, and Retry Logic");

        return builder.routes()
                .route("auth-password-reset", r -> r
                        .path("/api/auth/password/reset-request", "/api/auth/password/reset")
                        .filters(f -> f.retry(config -> config.setRetries(2)))
                        .uri("lb://AUTH-USER-SERVICE"))

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
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, false)))
                        .uri("lb://AUTH-USER-SERVICE"))

                .route("student-service", r -> r
                        .path("/api/student/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://AUTH-USER-SERVICE"))

                .route("teacher-service", r -> r
                        .path("/api/teacher/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://AUTH-USER-SERVICE"))

                .route("parent-service", r -> r
                        .path("/api/parent/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://AUTH-USER-SERVICE"))

                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://AUTH-USER-SERVICE"))

                .route("user-service", r -> r
                        .path("/user-service/api/users/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .circuitBreaker(c -> c
                                        .setName("auth-service-cb")
                                        .setFallbackUri("forward:/fallback/user"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://AUTH-USER-SERVICE"))

                .route("course-service", r -> r
                        .path("/api/courses/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("booking-service-cb")
                                        .setFallbackUri("forward:/fallback/course"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://LEARNING-MANAGEMENT-SERVICE"))

                .route("booking-service", r -> r
                        .path("/api/bookings/**", "/api/sessions/**", "/api/availability/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("booking-service-cb")
                                        .setFallbackUri("forward:/fallback/booking"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://LEARNING-MANAGEMENT-SERVICE"))

                .route("demo-service", r -> r
                        .path("/api/demos/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("booking-service-cb")
                                        .setFallbackUri("forward:/fallback/demo"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://LEARNING-MANAGEMENT-SERVICE"))

                .route("communication-ws", r -> r
                        .path("/ws-messaging/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("video-service-cb")
                                        .setFallbackUri("forward:/fallback/websocket")))
                        .uri("lb:ws://COMMUNICATION-SERVICE"))

                .route("video-service", r -> r
                        .path("/api/video/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("video-service-cb")
                                        .setFallbackUri("forward:/fallback/video"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://COMMUNICATION-SERVICE"))

                .route("messaging-service", r -> r
                        .path("/api/messages/**", "/api/conversations/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("messaging-service-cb")
                                        .setFallbackUri("forward:/fallback/message"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://COMMUNICATION-SERVICE"))

                .route("payment-service", r -> r
                        .path("/api/payments/**", "/api/refunds/**")
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
                                        .setBackoff(Duration.ofMillis(200), Duration.ofMillis(2000), 2, false)))
                        .uri("lb://FINANCIAL-SERVICE"))

                .route("payout-service", r -> r
                        .path("/api/payouts/**", "/api/earnings/**")
                        .filters(f -> f
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(paymentRateLimiter)
                                        .setKeyResolver(userKeyResolver)
                                        .setDenyEmptyKey(false))
                                .circuitBreaker(c -> c
                                        .setName("payment-service-cb")
                                        .setFallbackUri("forward:/fallback/payout"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://FINANCIAL-SERVICE"))

                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("notification-service-cb")
                                        .setFallbackUri("forward:/fallback/notification"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://NOTIFICATION-SERVICE"))

                .route("integration-service", r -> r
                        .path("/api/files/**", "/api/calendar/**", "/api/referrals/**", "/api/analytics/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("integration-service-cb")
                                        .setFallbackUri("forward:/fallback/integration"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://INTEGRATION-SERVICE"))

                .route("content-service", r -> r
                        .path("/api/recordings/**", "/api/reviews/**", "/api/materials/**", "/api/assignments/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("content-service-cb")
                                        .setFallbackUri("forward:/fallback/content"))
                                .retry(config -> config.setRetries(2)))
                        .uri("lb://CONTENT-SERVICE"))

                .route("actuator", r -> r
                        .path("/actuator/**")
                        .filters(f -> f.retry(config -> config.setRetries(1)))
                        .uri("lb://AUTH-USER-SERVICE"))

                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        log.info("🔧 Configuring Default Circuit Breaker Settings");
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50.0f)
                        .slowCallRateThreshold(50.0f)
                        .slowCallDurationThreshold(Duration.ofSeconds(2))
                        .permittedNumberOfCallsInHalfOpenState(5)
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .recordExceptions(Exception.class, RuntimeException.class)
                        .ignoreExceptions(IllegalArgumentException.class)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(5))
                        .cancelRunningFuture(true)
                        .build())
                .build());
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> paymentCircuitBreakerCustomizer() {
        log.info("🔧 Configuring Payment Circuit Breaker Settings");
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
        log.info("🔧 Configuring Video Circuit Breaker Settings");
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
        log.info("🔧 Configuring Messaging Circuit Breaker Settings");
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
}
