package com.tcon.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Generic event publisher for Kafka-based event streaming.
 * Use this to publish domain events to Kafka topics.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;

    /**
     * Publish an event to a specific Kafka topic
     *
     * @param topic The Kafka topic name
     * @param event The event to publish
     */
    public void publish(String topic, BaseEvent event) {
        try {
            log.info("Publishing event: {} to topic: {}", event.getEventType(), topic);

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(topic, event.getEventId(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Event published successfully: {} to partition: {} with offset: {}",
                            event.getEventType(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish event: {} to topic: {}. Error: {}",
                            event.getEventType(), topic, ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error publishing event: {} to topic: {}", event.getEventType(), topic, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    /**
     * Publish an event with a custom key
     *
     * @param topic The Kafka topic name
     * @param key   The message key (for partitioning)
     * @param event The event to publish
     */
    public void publish(String topic, String key, BaseEvent event) {
        try {
            log.info("Publishing event: {} with key: {} to topic: {}",
                    event.getEventType(), key, topic);

            CompletableFuture<SendResult<String, BaseEvent>> future =
                    kafkaTemplate.send(topic, key, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Event published successfully with key: {}", key);
                } else {
                    log.error("Failed to publish event with key: {}", key, ex);
                }
            });

        } catch (Exception e) {
            log.error("Error publishing event with key: {} to topic: {}", key, topic, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    /**
     * Publish event synchronously (blocks until confirmation)
     *
     * @param topic The Kafka topic name
     * @param event The event to publish
     * @return SendResult containing metadata
     */
    public SendResult<String, BaseEvent> publishSync(String topic, BaseEvent event) {
        try {
            log.info("Publishing event synchronously: {} to topic: {}",
                    event.getEventType(), topic);

            SendResult<String, BaseEvent> result =
                    kafkaTemplate.send(topic, event.getEventId(), event).get();

            log.info("Event published synchronously: {}", event.getEventType());
            return result;

        } catch (Exception e) {
            log.error("Error publishing event synchronously: {}", event.getEventType(), e);
            throw new RuntimeException("Failed to publish event synchronously", e);
        }
    }
}
