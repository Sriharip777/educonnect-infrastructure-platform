package com.tcon.events.events;

import com.tcon.events.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationEvent extends BaseEvent {

    private static final long serialVersionUID = 1L;

    private String recipientEmail;
    private String subject;
    private String message;
    private String notificationType; // EMAIL, PUSH, SMS
    private String referenceId;      // materialId, courseId, bookingId
    private String referenceType;    // MATERIAL, COURSE, BOOKING, SESSION

    // ✅ FIX #1: Implements abstract getEventType() from BaseEvent
    @Override
    public String getEventType() {
        return "NOTIFICATION_SENT";
    }

    // ✅ Convenience constructor — 3-arg (used in MaterialEventPublisher directly)
    public NotificationEvent(String recipientEmail,
                             String subject,
                             String message) {
        super();
        this.recipientEmail   = recipientEmail;
        this.subject          = subject;
        this.message          = message;
        this.notificationType = "EMAIL";
        this.setSource("content-service");
        // ✅ FIX #2: Correct call — static method on BaseEvent, NOT this.initializeEvent()
        BaseEvent.initializeDefaults(this);
    }

    // ✅ Full constructor
    public NotificationEvent(String recipientEmail,
                             String subject,
                             String message,
                             String notificationType,
                             String referenceId,
                             String referenceType,
                             String source) {
        super();
        this.recipientEmail   = recipientEmail;
        this.subject          = subject;
        this.message          = message;
        this.notificationType = notificationType;
        this.referenceId      = referenceId;
        this.referenceType    = referenceType;
        this.setSource(source);
        BaseEvent.initializeDefaults(this);
    }

    // ✅ Static factory — used from content-service for material notifications
    public static NotificationEvent create(String recipientEmail,
                                           String subject,
                                           String message,
                                           String referenceId) {
        NotificationEvent event = new NotificationEvent();
        event.setRecipientEmail(recipientEmail);
        event.setSubject(subject);
        event.setMessage(message);
        event.setNotificationType("EMAIL");
        event.setReferenceId(referenceId);
        event.setReferenceType("MATERIAL");
        event.setSource("content-service");
        // ✅ FIX #2: Static call — BaseEvent.initializeDefaults(event)
        BaseEvent.initializeDefaults(event);
        return event;
    }

    // ✅ Factory for booking notifications
    public static NotificationEvent forBooking(String recipientEmail,
                                               String subject,
                                               String message,
                                               String bookingId) {
        NotificationEvent event = new NotificationEvent();
        event.setRecipientEmail(recipientEmail);
        event.setSubject(subject);
        event.setMessage(message);
        event.setNotificationType("EMAIL");
        event.setReferenceId(bookingId);
        event.setReferenceType("BOOKING");
        event.setSource("booking-service");
        BaseEvent.initializeDefaults(event);
        return event;
    }

    // ✅ Factory for session notifications
    public static NotificationEvent forSession(String recipientEmail,
                                               String subject,
                                               String message,
                                               String sessionId) {
        NotificationEvent event = new NotificationEvent();
        event.setRecipientEmail(recipientEmail);
        event.setSubject(subject);
        event.setMessage(message);
        event.setNotificationType("EMAIL");
        event.setReferenceId(sessionId);
        event.setReferenceType("SESSION");
        event.setSource("session-service");
        BaseEvent.initializeDefaults(event);
        return event;
    }

    // ✅ Factory for payment notifications
    public static NotificationEvent forPayment(String recipientEmail,
                                               String subject,
                                               String message,
                                               String paymentId) {
        NotificationEvent event = new NotificationEvent();
        event.setRecipientEmail(recipientEmail);
        event.setSubject(subject);
        event.setMessage(message);
        event.setNotificationType("EMAIL");
        event.setReferenceId(paymentId);
        event.setReferenceType("PAYMENT");
        event.setSource("payment-service");
        BaseEvent.initializeDefaults(event);
        return event;
    }
}