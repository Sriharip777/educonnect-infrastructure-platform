package com.tcon.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Configuration
public class RateLimitConfig {

    // ===== Key Resolvers =====

    /**
     * Primary Key Resolver - Based on User ID or IP
     */
    @Primary
    @Bean
    public KeyResolver userKeyResolver() {
        log.info("🔧 Configuring User-based Rate Limiting Key Resolver");

        return exchange -> {
            // Try to get user ID from header
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");

            if (userId != null && !userId.isEmpty()) {
                log.debug("Rate limiting by User ID: {}", userId);
                return Mono.just("user:" + userId);
            }

            // Fallback to IP address
            String ipAddress = Objects.requireNonNull(
                    exchange.getRequest().getRemoteAddress()
            ).getAddress().getHostAddress();

            log.debug("Rate limiting by IP: {}", ipAddress);
            return Mono.just("ip:" + ipAddress);
        };
    }

    /**
     * API Key based Key Resolver
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        log.info("🔧 Configuring API Key-based Rate Limiting Key Resolver");

        return exchange -> {
            String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");

            if (apiKey != null && !apiKey.isEmpty()) {
                return Mono.just("apikey:" + apiKey);
            }

            // Fallback to IP
            return Mono.just("ip:" + Objects.requireNonNull(
                    exchange.getRequest().getRemoteAddress()
            ).getAddress().getHostAddress());
        };
    }

    /**
     * Path-based Key Resolver
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }

    // ===== Rate Limiters =====

    /**
     * Default Rate Limiter - Used by Gateway Auto Configuration
     * MUST BE MARKED AS @Primary
     */
    @Primary
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        log.info("🔧 [PRIMARY] Default Redis Rate Limiter: 100 req/s, burst: 200");
        return new RedisRateLimiter(100, 200, 1);
    }

    /**
     * Restrictive Rate Limiter for Payment/Financial operations
     */
    @Bean
    @Qualifier("paymentRateLimiter")
    public RedisRateLimiter paymentRateLimiter() {
        log.info("🔧 Payment Redis Rate Limiter: 10 req/s, burst: 20");
        return new RedisRateLimiter(10, 20, 1);
    }

    /**
     * Restrictive Rate Limiter for Auth operations (prevent brute force)
     */
    @Bean
    @Qualifier("authRateLimiter")
    public RedisRateLimiter authRateLimiter() {
        log.info("🔧 Auth Redis Rate Limiter: 5 req/s, burst: 10");
        return new RedisRateLimiter(5, 10, 1);
    }

    /**
     * Lenient Rate Limiter for public/read-only operations
     */
    @Bean
    @Qualifier("publicRateLimiter")
    public RedisRateLimiter publicRateLimiter() {
        log.info("🔧 Public Redis Rate Limiter: 1000 req/s, burst: 2000");
        return new RedisRateLimiter(1000, 2000, 1);
    }
}
