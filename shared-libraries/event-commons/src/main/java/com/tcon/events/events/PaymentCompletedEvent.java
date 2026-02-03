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
 * Event published when a payment is successfully completed
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentCompletedEvent extends BaseEvent {

    private String paymentId;
    private String bookingId;
    private String studentId;
    private String teacherId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod; // STRIPE, RAZORPAY
    private String transactionId; // Gateway transaction ID
    private String paymentGateway;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paidAt;

    private BigDecimal platformCommission;
    private BigDecimal teacherEarnings;
    private String paymentType; // FULL, INSTALLMENT

    @Override
    public String getEventType() {
        return "PAYMENT_COMPLETED";
    }
}

