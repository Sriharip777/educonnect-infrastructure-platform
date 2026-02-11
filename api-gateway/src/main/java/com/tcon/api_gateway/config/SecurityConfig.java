// java
package com.tcon.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("🔒 Configuring API Gateway Security");

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .cors(ServerHttpSecurity.CorsSpec::disable) // Let YAML handle CORS
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/actuator/**", "/fallback/**").permitAll()
                        .pathMatchers("/api/auth/**", "/auth/**").permitAll()
                        .anyExchange().permitAll()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);
        log.info("✅ Security configured - CORS handled by Gateway YAML");
        return http.build();
    }

//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        log.info("🌐 Registering CorsWebFilter for API Gateway");
//
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // Allow specific local dev origins and wildcard patterns for deployed origins
//        configuration.setAllowedOrigins(Arrays.asList(
//                "http://localhost:3000",
//                "http://localhost:5173",
//                "http://localhost:4200",
//                "http://127.0.0.1:3000",
//                "http://127.0.0.1:5173",
//                "http://127.0.0.1:4200"
//        ));

        // Allow patterns (useful for dynamic or deployed origins); keeps credentials true
//        configuration.setAllowedOriginPatterns(List.of("*"));
//
//        configuration.setAllowedMethods(Arrays.asList(
//                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
//        ));
//
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setExposedHeaders(Arrays.asList(
//                "Authorization",
//                "Content-Type",
//                "X-Total-Count",
//                "X-Request-Id",
//                "X-User-Id",
//                "X-User-Role",
//                "X-User-Email"
//        ));
//
//        configuration.setAllowCredentials(true);
//        configuration.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        log.info("✅ CORS configured");
//        return new CorsWebFilter(source);
//    }
}