package com.tcon.events.events;

import com.tcon.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Event published when a refund is requested
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RefundRequestedEvent extends BaseEvent {

    private String refundId;
    private String paymentId;
    private String bookingId;
    private String studentId;
    private String teacherId;
    private BigDecimal refundAmount;
    private String currency;
    private String refundReason;
    private String requestedBy; // USER_ID
    private Boolean isAutoRefund; // True for >24hrs cancellation

    @Override
    public String getEventType() {
        return "REFUND_REQUESTED";
    }
}
