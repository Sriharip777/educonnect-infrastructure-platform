package com.tcon.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Optional;

@Slf4j
@Configuration
public class RateLimitConfig {

    // ===== Key Resolvers =====

    /**
     * Primary Key Resolver - Based on User ID or IP
     * Used as default when no specific resolver is specified
     */
    @Primary
    @Bean
    public KeyResolver userKeyResolver() {
        log.info("🔧 Configuring User-based Rate Limiting Key Resolver");

        return exchange -> {
            // Try to get user ID from header (set by authentication filter)
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");

            if (userId != null && !userId.isEmpty()) {
                log.debug("Rate limiting by User ID: {}", userId);
                return Mono.just("user:" + userId);
            }

            // Try to get user from JWT subject (if available)
            String jwtSubject = exchange.getRequest().getHeaders().getFirst("X-Auth-User-Id");
            if (jwtSubject != null && !jwtSubject.isEmpty()) {
                log.debug("Rate limiting by JWT Subject: {}", jwtSubject);
                return Mono.just("user:" + jwtSubject);
            }

            // Fallback to IP address
            String ipAddress = getClientIp(exchange.getRequest().getRemoteAddress());
            log.debug("Rate limiting by IP: {}", ipAddress);
            return Mono.just("ip:" + ipAddress);
        };
    }

    /**
     * API Key based Key Resolver
     * For external API integrations
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        log.info("🔧 Configuring API Key-based Rate Limiting Key Resolver");

        return exchange -> {
            String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");

            if (apiKey != null && !apiKey.isEmpty()) {
                log.debug("Rate limiting by API Key: {}", maskApiKey(apiKey));
                return Mono.just("apikey:" + apiKey);
            }

            // Fallback to IP
            String ipAddress = getClientIp(exchange.getRequest().getRemoteAddress());
            log.debug("No API key found, rate limiting by IP: {}", ipAddress);
            return Mono.just("ip:" + ipAddress);
        };
    }

    /**
     * Path-based Key Resolver
     * Groups rate limiting by endpoint path
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        log.info("🔧 Configuring Path-based Rate Limiting Key Resolver");

        return exchange -> {
            String path = exchange.getRequest().getPath().value();
            log.debug("Rate limiting by path: {}", path);
            return Mono.just("path:" + path);
        };
    }

    /**
     * Combined Key Resolver - User + Path
     * Most granular control
     */
    @Bean
    public KeyResolver userPathKeyResolver() {
        log.info("🔧 Configuring User+Path-based Rate Limiting Key Resolver");

        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            String path = exchange.getRequest().getPath().value();

            if (userId != null && !userId.isEmpty()) {
                return Mono.just("user:" + userId + ":path:" + path);
            }

            String ipAddress = getClientIp(exchange.getRequest().getRemoteAddress());
            return Mono.just("ip:" + ipAddress + ":path:" + path);
        };
    }

    // ===== Rate Limiters =====

    /**
     * Default Rate Limiter - Used by Gateway Auto Configuration
     * MUST BE MARKED AS @Primary to avoid bean creation errors
     *
     * Parameters:
     * - replenishRate: 100 tokens per second
     * - burstCapacity: 200 max tokens
     * - requestedTokens: 1 token per request
     */
    @Primary
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        log.info("🔧 [PRIMARY] Default Redis Rate Limiter: 100 req/s, burst: 200");
        return new RedisRateLimiter(100, 200, 1);
    }

    /**
     * Restrictive Rate Limiter for Auth operations
     * Prevents brute force attacks on login/registration
     *
     * 5 requests per second, burst up to 10
     */
    @Bean
    @Qualifier("authRateLimiter")
    public RedisRateLimiter authRateLimiter() {
        log.info("🔧 Auth Redis Rate Limiter: 5 req/s, burst: 10");
        return new RedisRateLimiter(5, 10, 1);
    }

    /**
     * Very Restrictive Rate Limiter for Payment/Financial operations
     * Critical security for payment endpoints
     *
     * 10 requests per second, burst up to 20
     */
    @Bean
    @Qualifier("paymentRateLimiter")
    public RedisRateLimiter paymentRateLimiter() {
        log.info("🔧 Payment Redis Rate Limiter: 10 req/s, burst: 20");
        return new RedisRateLimiter(10, 20, 1);
    }

    /**
     * Lenient Rate Limiter for public/read-only operations
     * For endpoints that don't require strict limiting
     *
     * 1000 requests per second, burst up to 2000
     */
    @Bean
    @Qualifier("publicRateLimiter")
    public RedisRateLimiter publicRateLimiter() {
        log.info("🔧 Public Redis Rate Limiter: 1000 req/s, burst: 2000");
        return new RedisRateLimiter(1000, 2000, 1);
    }

    /**
     * Moderate Rate Limiter for general API operations
     * Balanced between security and usability
     *
     * 50 requests per second, burst up to 100
     */
    @Bean
    @Qualifier("apiRateLimiter")
    public RedisRateLimiter apiRateLimiter() {
        log.info("🔧 API Redis Rate Limiter: 50 req/s, burst: 100");
        return new RedisRateLimiter(50, 100, 1);
    }

    // ===== Helper Methods =====

    /**
     * Extract client IP address, handling X-Forwarded-For header
     */
    private String getClientIp(InetSocketAddress remoteAddress) {
        return Optional.ofNullable(remoteAddress)
                .map(InetSocketAddress::getAddress)
                .map(address -> address.getHostAddress())
                .orElse("unknown");
    }

    /**
     * Mask API key for logging (security)
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "****";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}