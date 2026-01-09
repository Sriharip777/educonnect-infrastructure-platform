package com.tcon.util;

import java.util.regex.Pattern;

/**
 * Utility class for validation operations
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9]{10,15}$"
    );

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)?([a-z0-9-]+\\.)+[a-z]{2,}(/.*)?$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate password strength
     * Must contain: 1 uppercase, 1 lowercase, 1 digit, 1 special char, min 8 chars
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validate URL
     */
    public static boolean isValidURL(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }

    /**
     * Check if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    /**
     * Validate string length
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) return false;
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate number is positive
     */
    public static boolean isPositive(Number number) {
        return number != null && number.doubleValue() > 0;
    }

    /**
     * Validate number is non-negative
     */
    public static boolean isNonNegative(Number number) {
        return number != null && number.doubleValue() >= 0;
    }

    /**
     * Validate number is within range
     */
    public static boolean isInRange(Number number, double min, double max) {
        if (number == null) return false;
        double value = number.doubleValue();
        return value >= min && value <= max;
    }

    /**
     * Sanitize string (remove special characters)
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        return input.replaceAll("[^a-zA-Z0-9\\s-_]", "");
    }

    /**
     * Validate UUID format
     */
    public static boolean isValidUUID(String uuid) {
        if (uuid == null) return false;
        Pattern uuidPattern = Pattern.compile(
                "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        );
        return uuidPattern.matcher(uuid).matches();
    }

    private ValidationUtil() {
        throw new IllegalStateException("Utility class");
    }
}
