package com.tcon.events.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcon.events.BaseEvent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a payment is successfully processed.
 * This event triggers:
 * - Booking confirmation in learning-management-service
 * - Email notification to student and teacher
 * - Teacher earnings calculation for payout
 * - Analytics tracking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentSuccessEvent extends BaseEvent {

    /**
     * Unique payment identifier
     */
    @NotNull
    @JsonProperty("payment_id")
    private String paymentId;

    /**
     * Associated booking ID
     */
    @NotNull
    @JsonProperty("booking_id")
    private String bookingId;

    /**
     * Student who made the payment
     */
    @NotNull
    @JsonProperty("student_id")
    private String studentId;

    /**
     * Teacher who will receive earnings
     */
    @NotNull
    @JsonProperty("teacher_id")
    private String teacherId;

    /**
     * Total payment amount
     */
    @NotNull
    @Positive
    @JsonProperty("amount")
    private BigDecimal amount;

    /**
     * Currency code (USD, INR, EUR, etc.)
     */
    @NotNull
    @JsonProperty("currency")
    private String currency;

    /**
     * Payment method: STRIPE, RAZORPAY, CREDIT_CARD, DEBIT_CARD, UPI
     */
    @JsonProperty("payment_method")
    private String paymentMethod;

    /**
     * Transaction ID from payment gateway
     */
    @JsonProperty("transaction_id")
    private String transactionId;

    /**
     * Payment gateway used (STRIPE or RAZORPAY)
     */
    @JsonProperty("payment_gateway")
    private String paymentGateway;

    /**
     * When the payment was successfully processed
     */
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("paid_at")
    private LocalDateTime paidAt;

    /**
     * Platform commission (10% for recurring, 15% for non-recurring)
     */
    @JsonProperty("platform_commission")
    private BigDecimal platformCommission;

    /**
     * Amount teacher will receive after commission
     */
    @JsonProperty("teacher_earnings")
    private BigDecimal teacherEarnings;

    /**
     * Payment type: FULL, INSTALLMENT, DEMO
     */
    @JsonProperty("payment_type")
    private String paymentType;

    /**
     * Course ID if applicable
     */
    @JsonProperty("course_id")
    private String courseId;

    /**
     * Session ID if applicable
     */
    @JsonProperty("session_id")
    private String sessionId;

    /**
     * Whether this is a recurring payment
     */
    @JsonProperty("is_recurring")
    private Boolean isRecurring;

    /**
     * Current installment number
     */
    @JsonProperty("installment_number")
    private Integer installmentNumber;

    /**
     * Total number of installments
     */
    @JsonProperty("total_installments")
    private Integer totalInstallments;

    /**
     * Student email for receipt
     */
    @JsonProperty("student_email")
    private String studentEmail;

    /**
     * Teacher email for notification
     */
    @JsonProperty("teacher_email")
    private String teacherEmail;

    /**
     * Payment receipt URL
     */
    @JsonProperty("receipt_url")
    private String receiptUrl;

    /**
     * Payment status from gateway
     */
    @JsonProperty("gateway_status")
    private String gatewayStatus;

    /**
     * Customer IP address (for fraud detection)
     */
    @JsonProperty("customer_ip")
    private String customerIp;

    @Override
    public String getEventType() {
        return "PAYMENT_SUCCESS";
    }

    /**
     * Check if this is the final installment
     */
    public boolean isFinalInstallment() {
        return installmentNumber != null &&
                totalInstallments != null &&
                installmentNumber.equals(totalInstallments);
    }

    /**
     * Get commission percentage
     */
    public double getCommissionPercentage() {
        return (isRecurring != null && isRecurring) ? 0.10 : 0.15;
    }
}
