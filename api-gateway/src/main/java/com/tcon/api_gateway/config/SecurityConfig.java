// java
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

}