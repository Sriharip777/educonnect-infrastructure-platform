package com.tcon.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when validation fails
 */
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }

    public ValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    public ValidationException(List<String> errors) {
        super("Validation failed");
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
}
