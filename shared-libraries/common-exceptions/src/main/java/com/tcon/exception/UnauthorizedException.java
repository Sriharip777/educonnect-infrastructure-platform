package com.tcon.exception;

/**
 * Exception thrown when user is not authorized
 */
public class UnauthorizedException extends RuntimeException {

    private final String userId;
    private final String action;

    public UnauthorizedException(String message) {
        super(message);
        this.userId = null;
        this.action = null;
    }

    public UnauthorizedException(String message, String userId, String action) {
        super(message);
        this.userId = userId;
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }
}
