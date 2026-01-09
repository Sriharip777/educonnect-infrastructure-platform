package com.tcon.exception;

/**
 * Exception thrown when payment processing fails
 */
public class PaymentException extends RuntimeException {

    private final String paymentId;
    private final String errorCode;

    public PaymentException(String message) {
        super(message);
        this.paymentId = null;
        this.errorCode = null;
    }

    public PaymentException(String message, String paymentId) {
        super(message);
        this.paymentId = paymentId;
        this.errorCode = null;
    }

    public PaymentException(String message, String paymentId, String errorCode) {
        super(message);
        this.paymentId = paymentId;
        this.errorCode = errorCode;
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
        this.paymentId = null;
        this.errorCode = null;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
