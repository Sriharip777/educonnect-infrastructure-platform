package com.tcon.api_gateway.config;

import com.tcon.api_gateway.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security Configuration for API Gateway
 * Handles authentication, authorization, CORS, and WebSocket security
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange
                        // Public endpoints - Authentication
                        .pathMatchers("/api/auth/login", "/api/auth/register", "/api/auth/forgot-password").permitAll()
                        .pathMatchers("/api/auth/refresh-token", "/api/auth/verify-email").permitAll()

                        // Public endpoints - Health & Monitoring
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/eureka/**").permitAll()

                        // WebSocket endpoints - Allow upgrade requests
                        .pathMatchers("/communication/ws-messaging/**").permitAll()
                        .pathMatchers("/ws-messaging/**").permitAll()

                        // Public API endpoints (if any)
                        .pathMatchers("/api/public/**").permitAll()

                        // All other endpoints require authentication
                        .anyExchange().authenticated()
                )
                .addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow frontend URLs
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",      // React default
                "http://localhost:5173",      // Vite default
                "http://localhost:4200",      // Angular default
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173"
        ));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Expose headers that frontend might need
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count",
                "X-Request-Id"
        ));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
