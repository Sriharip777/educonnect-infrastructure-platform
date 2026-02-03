package com.tcon.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("🔒 Configuring API Gateway Security with CORS and CSRF disabled");

        http
                // Disable CSRF for stateless API
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Permit all exchanges - JWT validation handled downstream
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/**", "/fallback/**").permitAll()
                        .anyExchange().permitAll()
                )

                // Disable form login and HTTP basic
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        log.info("✅ API Gateway Security configured - Authentication delegated to downstream services");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("🌐 Configuring CORS for API Gateway");

        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:4200",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:4200"
        ));

        // Or use patterns for more flexibility
        // configuration.setAllowedOriginPatterns(List.of("*"));

        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Allowed headers
        configuration.setAllowedHeaders(List.of("*"));

        // Exposed headers
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count",
                "X-Request-Id",
                "X-User-Id",
                "X-User-Role",
                "X-User-Email"
        ));

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        log.info("✅ CORS configured: Origins = {}", configuration.getAllowedOrigins());
        return source;
    }
}
