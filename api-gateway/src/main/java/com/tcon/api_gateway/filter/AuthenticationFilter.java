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
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/api/auth/verify-email",
            "/api/auth/password/reset-request",
            "/api/auth/password/reset",
            "/actuator/**",
            "/eureka/**",
            "/api/public/**",
            "/ws-messaging/info"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().toString();

        log.debug("🌐 API Gateway: {} {}", method, path);

        // ✅ CRITICAL: Skip OPTIONS requests for CORS preflight
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.debug("✅ OPTIONS request - skipping authentication for CORS preflight");
            return chain.filter(exchange);
        }

        // Skip JWT validation for public paths
        if (isPublicPath(path)) {
            log.debug("✅ Public path: {}", path);
            return chain.filter(exchange);
        }

        // ✅ NEW: Handle WebSocket paths (token in query parameter)
        if (path.startsWith("/ws-messaging")) {
            return handleWebSocketAuth(exchange, chain);
        }

        // Handle regular HTTP requests (token in Authorization header)
        return handleHttpAuth(exchange, chain);
    }

    /**
     * ✅ NEW METHOD: Handle WebSocket authentication (token in query parameter)
     */
    private Mono<Void> handleWebSocketAuth(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Extract token from query parameter
        List<String> tokenParams = request.getQueryParams().get("access_token");

        if (tokenParams == null || tokenParams.isEmpty()) {
            log.warn("❌ Missing access_token query parameter for WebSocket: {}", path);
            return onError(exchange, "Missing access_token query parameter", HttpStatus.UNAUTHORIZED);
        }

        String token = tokenParams.get(0).trim().replaceAll("\\s+", "");
        log.debug("🔍 WebSocket JWT token (length: {})", token.length());

        try {
            // Validate JWT token
            Claims claims = validateToken(token);
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            String email = claims.get("email", String.class);

            log.info("✅ WebSocket JWT VALID: user={}, role={}", email, role);

            // Add user info to headers for downstream services
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .header("X-User-Email", email)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)  // ✅ Add Authorization header
                    .build();

            log.debug("➡️ Forwarding WebSocket to communication-service with user headers");
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

    /**
     * Handle regular HTTP authentication (token in Authorization header)
     */
    private Mono<Void> handleHttpAuth(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().toString();

        // Extract Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("❌ Missing Authorization header for: {} {}", method, path);
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        // Extract token and remove ALL whitespace
        String token = authHeader.substring(7).trim().replaceAll("\\s+", "");
        log.debug("🔍 Extracted JWT token (length: {})", token.length());

        try {
            // Validate JWT token
            Claims claims = validateToken(token);
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            String email = claims.get("email", String.class);

            log.info("✅ JWT VALID: user={}, role={}", email, role);

            // Add user info to headers for downstream services
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", role)
                    .header("X-User-Email", email)
                    .build();

            log.debug("➡️ Forwarding to downstream service with user headers");
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