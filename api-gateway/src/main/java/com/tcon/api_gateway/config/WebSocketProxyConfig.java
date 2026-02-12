package com.tcon.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Configuration
public class WebSocketProxyConfig {

    /**
     * Custom headers filter for WebSocket proxying
     * Preserves WebSocket upgrade headers
     */
    @Bean
    public HttpHeadersFilter webSocketHeadersFilter() {
        return new HttpHeadersFilter() {
            @Override
            public HttpHeaders filter(HttpHeaders input, ServerWebExchange exchange) {
                HttpHeaders filtered = new HttpHeaders();
                filtered.putAll(input);

                // Preserve WebSocket upgrade headers
                String upgrade = input.getFirst(HttpHeaders.UPGRADE);
                if ("websocket".equalsIgnoreCase(upgrade)) {
                    log.debug("🔄 [Gateway] Preserving WebSocket upgrade headers");

                    // Keep essential WebSocket headers
                    preserveHeader(input, filtered, HttpHeaders.UPGRADE);
                    preserveHeader(input, filtered, HttpHeaders.CONNECTION);
                    preserveHeader(input, filtered, "Sec-WebSocket-Key");
                    preserveHeader(input, filtered, "Sec-WebSocket-Version");
                    preserveHeader(input, filtered, "Sec-WebSocket-Protocol");
                    preserveHeader(input, filtered, "Sec-WebSocket-Extensions");
                }

                return filtered;
            }

            private void preserveHeader(HttpHeaders input, HttpHeaders output, String headerName) {
                String value = input.getFirst(headerName);
                if (value != null) {
                    output.set(headerName, value);
                }
            }

            @Override
            public boolean supports(Type type) {
                return type == Type.RESPONSE;
            }
        };
    }
}