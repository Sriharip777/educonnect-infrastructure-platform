package com.tcon.events.events;

import com.tcon.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Event published when a payment fails
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentFailedEvent extends BaseEvent {

    private String paymentId;
    private String bookingId;
    private String studentId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String failureReason;
    private String errorCode;
    private Integer retryCount;

    @Override
    public String getEventType() {
        return "PAYMENT_FAILED";
    }
}
