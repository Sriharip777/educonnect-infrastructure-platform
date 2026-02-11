package com.tcon.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class CorsWebFilterConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)  // ✅ HIGHEST priority - runs FIRST
    public CorsWebFilter corsWebFilter() {
        log.info("🌐 [CORS] Configuring CorsWebFilter with HIGHEST_PRECEDENCE");

        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allowed origins
        corsConfig.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:4200",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:4200"
        ));

        // Allowed methods
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Allowed headers
        corsConfig.setAllowedHeaders(List.of("*"));

        // Exposed headers
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Content-Length",
                "X-User-Id",
                "X-User-Role",
                "X-User-Email"
        ));

        // Allow credentials
        corsConfig.setAllowCredentials(true);

        // Max age
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        log.info("✅ [CORS] CorsWebFilter configured successfully");

        return new CorsWebFilter(source);
    }
}