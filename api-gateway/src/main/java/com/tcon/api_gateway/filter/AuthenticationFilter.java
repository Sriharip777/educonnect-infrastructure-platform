package com.tcon.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> PUBLIC_PATHS = List.of(
            // Auth public endpoints
            "/api/auth/register",
            "/api/auth/login",

            // ✅ NEWLY ADDED (from second code)
            "/api/teacher/**",

            "/api/auth/forgot-password",
            "/api/auth/refresh-token",
            "/api/auth/verify-email",
            "/api/auth/reset-password",
            "/api/auth/password/reset-request",
            "/api/auth/password/reset",
            "/api/auth/health",
            "/api/courses/public/published",
            "/api/grades/**",
            "/api/subjects/**",
            "/api/topics/**",

            // ✅ FIX 1: Added validate-token so LMS Feign calls are not blocked
            "/api/auth/validate-token",

            // ✅ FIX 2: Added fallback so circuit breaker fallback POST is not blocked
            "/fallback/**",

            // System
            "/actuator/**",
            "/eureka/**",
            "/api/public/**",

            // WebSocket
            "/ws-messaging/info",
            "/ws-messaging/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path   = request.getPath().value();
        String method = request.getMethod().toString();

        log.debug("🌐 API Gateway: {} {}", method, path);

        // Skip WebSocket paths
        if (path.startsWith("/ws-messaging")) {
            log.info("🔓 Skipping JWT for WebSocket path: {}", path);
            return chain.filter(exchange);
        }

        // ✅ Skip OPTIONS requests for CORS preflight
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.debug("✅ OPTIONS request - skipping authentication for CORS preflight");
            return chain.filter(exchange);
        }

        // Skip JWT validation for public paths
        if (isPublicPath(path)) {
            log.debug("✅ Public path: {}", path);
            return chain.filter(exchange);
        }

        // Handle WebSocket paths (token in query parameter)
        if (path.startsWith("/ws-messaging")) {
            return handleWebSocketAuth(exchange, chain);
        }

        // Handle regular HTTP requests
        return handleHttpAuth(exchange, chain);
    }

    private String normalizeRole(String role) {
        return role != null ? role.toUpperCase() : null;
    }

    private Mono<Void> handleWebSocketAuth(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        List<String> tokenParams = request.getQueryParams().get("access_token");

        if (tokenParams == null || tokenParams.isEmpty()) {
            log.warn("❌ Missing access_token query parameter for WebSocket: {}", path);
            return onError(exchange, "Missing access_token query parameter", HttpStatus.UNAUTHORIZED);
        }

        String token = tokenParams.get(0).trim().replaceAll("\\s+", "");
        log.debug("🔍 WebSocket JWT token (length: {})", token.length());

        try {
            Claims claims = validateToken(token);
            String userId = claims.getSubject();
            String role = normalizeRole(claims.get("role", String.class));

            if (!StringUtils.hasText(role)) {
                log.error("❌ Role missing in JWT");
                return onError(exchange, "Invalid token: role missing", HttpStatus.UNAUTHORIZED);
            }

            String email  = claims.get("email", String.class);

            log.info("✅ JWT VALID: user={}, role={}", email, role);

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .header("X-User-Email", email)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("❌ WebSocket JWT expired: {}", e.getMessage());
            return onError(exchange, "Token expired", HttpStatus.UNAUTHORIZED);
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.error("❌ Invalid WebSocket JWT signature: {}", e.getMessage());
            return onError(exchange, "Invalid token signature", HttpStatus.UNAUTHORIZED);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("❌ Malformed WebSocket JWT: {}", e.getMessage());
            return onError(exchange, "Malformed token", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("❌ WebSocket JWT validation failed: {}", e.getMessage());
            return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> handleHttpAuth(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path   = request.getPath().value();
        String method = request.getMethod().toString();

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("❌ Missing Authorization header for: {} {}", method, path);
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7).trim().replaceAll("\\s+", "");
        log.debug("🔍 Extracted JWT token (length: {})", token.length());

        try {
            Claims claims = validateToken(token);
            String userId = claims.getSubject();
            String role   = claims.get("role", String.class);
            String email  = claims.get("email", String.class);

            log.info("✅ JWT VALID: user={}, role={}", email, role);

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .header("X-User-Email", email)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("❌ JWT expired: {}", e.getMessage());
            return onError(exchange, "Token expired", HttpStatus.UNAUTHORIZED);
        } catch (io.jsonwebtoken.security.SecurityException e) {
            log.error("❌ Invalid JWT signature: {}", e.getMessage());
            return onError(exchange, "Invalid token signature", HttpStatus.UNAUTHORIZED);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("❌ Malformed JWT: {}", e.getMessage());
            return onError(exchange, "Malformed token", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("❌ JWT validation failed: {}", e.getMessage());
            return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        String errorBody = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\",\"status\":%d,\"path\":\"%s\",\"timestamp\":\"%s\"}",
                status.getReasonPhrase(),
                message,
                status.value(),
                exchange.getRequest().getPath().value(),
                java.time.LocalDateTime.now()
        );

        log.warn("⛔ Returning {} for {} {}",
                status,
                exchange.getRequest().getMethod(),
                exchange.getRequest().getPath().value());

        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorBody.getBytes())));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}