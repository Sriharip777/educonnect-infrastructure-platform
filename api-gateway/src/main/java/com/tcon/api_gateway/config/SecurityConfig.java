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



        http
                // CSRF is not needed for a stateless API gateway
                .csrf(ServerHttpSecurity.CsrfSpec::disable)



                .authorizeExchange(exchange -> exchange

                        // Allow all preflight requests
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()

                        .pathMatchers(
                                "/ws/**",
                                "/ws/support-chat/**"
                        ).permitAll()

                        // Health and circuit breaker fallbacks
                        .pathMatchers("/actuator/**", "/fallback/**").permitAll()

                        // Auth endpoints (login/registration/verification etc.)
                        .pathMatchers(
                                "/api/auth/**",
                                "/auth/**"
                        ).permitAll()

                        // Teacher public profile (if you want it public)
                        .pathMatchers("/api/teacher/profile/**").permitAll()

                        // Everything else is allowed for now (gateway is not enforcing auth)
                        .anyExchange().permitAll()
                )

                // Disable HTTP Basic and form login — not used on gateway
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable);


        return http.build();
    }
}