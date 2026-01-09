package com.tcon.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * Base class for event listeners.
 * Services can extend this to create specific event handlers.
 */
@Slf4j
public abstract class EventListener {

    /**
     * Handle incoming event
     *
     * @param event     The received event
     * @param partition The Kafka partition
     * @param offset    The message offset
     */
    protected void handleEvent(
            @Payload BaseEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received event: {} from partition: {} with offset: {}",
                event.getEventType(), partition, offset);

        try {
            processEvent(event);
            log.info("Successfully processed event: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Error processing event: {} with ID: {}",
                    event.getEventType(), event.getEventId(), e);
            handleProcessingError(event, e);
        }
    }

    /**
     * Process the event - to be implemented by subclasses
     *
     * @param event The event to process
     */
    protected abstract void processEvent(BaseEvent event);

    /**
     * Handle processing errors - can be overridden by subclasses
     *
     * @param event The event that failed to process
     * @param error The error that occurred
     */
    protected void handleProcessingError(BaseEvent event, Exception error) {
        log.error("Failed to process event: {}. Error: {}",
                event.getEventId(), error.getMessage());
        // Default: Log error. Subclasses can implement retry logic or dead letter queue
    }
}
