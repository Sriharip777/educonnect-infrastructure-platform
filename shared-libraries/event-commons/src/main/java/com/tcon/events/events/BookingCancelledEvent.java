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
 * Event published when a booking is cancelled
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookingCancelledEvent extends BaseEvent {

    private String bookingId;
    private String studentId;
    private String teacherId;
    private String sessionId;
    private String cancelledBy; // USER_ID who cancelled
    private String cancellationReason;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime scheduledAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime cancelledAt;

    private Boolean isEligibleForRefund;
    private BigDecimal refundAmount;
    private Integer hoursBeforeClass;

    @Override
    public String getEventType() {
        return "BOOKING_CANCELLED";
    }
}
