package com.tcon.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tcon.events.events.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserCreatedEvent.class,        name = "USER_CREATED"),
        @JsonSubTypes.Type(value = UserRegisteredEvent.class,     name = "USER_REGISTERED"),
        @JsonSubTypes.Type(value = TeacherApprovedEvent.class,    name = "TEACHER_APPROVED"),
        @JsonSubTypes.Type(value = BookingCreatedEvent.class,     name = "BOOKING_CREATED"),
        @JsonSubTypes.Type(value = BookingCancelledEvent.class,   name = "BOOKING_CANCELLED"),
        @JsonSubTypes.Type(value = ClassStartedEvent.class,       name = "CLASS_STARTED"),
        @JsonSubTypes.Type(value = ClassCompletedEvent.class,     name = "CLASS_COMPLETED"),
        @JsonSubTypes.Type(value = PaymentCompletedEvent.class,   name = "PAYMENT_COMPLETED"),
        @JsonSubTypes.Type(value = PaymentFailedEvent.class,      name = "PAYMENT_FAILED"),
        @JsonSubTypes.Type(value = RefundRequestedEvent.class,    name = "REFUND_REQUESTED"),
        @JsonSubTypes.Type(value = ReviewCreatedEvent.class,      name = "REVIEW_CREATED"),
        @JsonSubTypes.Type(value = ReferralCompletedEvent.class,  name = "REFERRAL_COMPLETED"),
        @JsonSubTypes.Type(value = RecordingAvailableEvent.class, name = "RECORDING_AVAILABLE"),
        @JsonSubTypes.Type(value = SessionScheduledEvent.class,   name = "SESSION_SCHEDULED"),
        @JsonSubTypes.Type(value = PaymentSuccessEvent.class,     name = "PAYMENT_SUCCESS"),
        // ✅ FIX: Added NotificationEvent to JsonSubTypes registry
        @JsonSubTypes.Type(value = NotificationEvent.class,       name = "NOTIFICATION_SENT")
})
public abstract class BaseEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String source;
    private String version;
    private String correlationId;

    // ✅ Abstract — every subclass MUST override this
    public abstract String getEventType();

    // ✅ Static helper — called as BaseEvent.initializeDefaults(this)
    public static void initializeDefaults(BaseEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }
        if (event.getTimestamp() == null) {
            event.setTimestamp(LocalDateTime.now());
        }
        if (event.getVersion() == null) {
            event.setVersion("1.0");
        }
    }
}