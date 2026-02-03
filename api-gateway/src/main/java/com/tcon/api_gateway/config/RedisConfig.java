package com.tcon.api_gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Reactive Redis Template for String-String operations
     */
    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        log.info("🔧 Configuring Reactive Redis Template for caching and session management");

        StringRedisSerializer serializer = new StringRedisSerializer();

        RedisSerializationContext<String, String> serializationContext =
                RedisSerializationContext.<String, String>newSerializationContext()
                        .key(serializer)
                        .value(serializer)
                        .hashKey(serializer)
                        .hashValue(serializer)
                        .build();

        ReactiveRedisTemplate<String, String> template =
                new ReactiveRedisTemplate<>(connectionFactory, serializationContext);

        log.info("✅ Reactive Redis Template configured successfully");
        return template;
    }

    /**
     * Reactive Redis Template for Object serialization (JSON)
     */
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisObjectTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        log.info("🔧 Configuring Reactive Redis Object Template with JSON serialization");

        StringRedisSerializer keySerializer = new StringRedisSerializer();

        // Configure Jackson ObjectMapper for proper JSON serialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisSerializationContext<String, Object> serializationContext =
                RedisSerializationContext.<String, Object>newSerializationContext()
                        .key(keySerializer)
                        .value(valueSerializer)
                        .hashKey(keySerializer)
                        .hashValue(valueSerializer)
                        .build();

        ReactiveRedisTemplate<String, Object> template =
                new ReactiveRedisTemplate<>(connectionFactory, serializationContext);

        log.info("✅ Reactive Redis Object Template configured successfully");
        return template;
    }
}
