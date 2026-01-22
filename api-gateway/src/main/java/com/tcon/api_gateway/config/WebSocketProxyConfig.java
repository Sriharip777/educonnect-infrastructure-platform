package com.tcon.api_gateway.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket Proxy Configuration
 * Customizes HttpClient for WebSocket connections through Gateway
 */
@Slf4j
@Configuration
public class WebSocketProxyConfig {

    @Bean
    public HttpClientCustomizer httpClientCustomizer() {
        return httpClient -> {
            log.info("Configuring HttpClient for WebSocket proxy support");

            return httpClient
                    // Connection timeout: 60 seconds
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)

                    // Keep connection alive
                    .option(ChannelOption.SO_KEEPALIVE, true)

                    // Response timeout: 60 seconds
                    .responseTimeout(Duration.ofSeconds(60))

                    // Add read/write timeout handlers
                    .doOnConnected(connection -> {
                        log.debug("WebSocket connection established, adding timeout handlers");
                        connection.addHandlerLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(60, TimeUnit.SECONDS));
                    })

                    // Log connection events
                    .doOnDisconnected(connection ->
                            log.debug("WebSocket connection disconnected")
                    );
        };
    }
}
