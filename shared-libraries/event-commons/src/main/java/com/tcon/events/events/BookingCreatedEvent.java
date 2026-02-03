package com.tcon.events.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tcon.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a class booking is created
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookingCreatedEvent extends BaseEvent {

    private String bookingId;
    private String studentId;
    private String teacherId;
    private String courseId;
    private String sessionId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledAt;

    private Integer durationMinutes;
    private BigDecimal amount;
    private String currency;
    private String bookingType; // SOLO, RECURRING, DEMO
    private Boolean requiresPayment;

    @Override
    public String getEventType() {
        return "BOOKING_CREATED";
    }
}
