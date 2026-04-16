package com.tcon.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        log.info("🔒 Configuring API Gateway Security");

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // ✅ Enable CORS (handled by CorsWebFilterConfig)
                .cors(cors -> {})

                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/actuator/**", "/fallback/**").permitAll()

                        // ✅ MODIFIED (added new endpoint inside existing matcher)
                        .pathMatchers(
                                "/api/auth/**",
                                "/auth/**",
                                "/api/teacher/profile/**"
                        ).permitAll()

                        .anyExchange().permitAll()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);

        log.info("✅ Security configured - CORS handled by CorsWebFilterConfig");

        return http.build();
    }
}