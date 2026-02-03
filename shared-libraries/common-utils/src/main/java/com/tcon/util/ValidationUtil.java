package com.tcon.util;

import org.apache.commons.lang3.StringUtils;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$");

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return StringUtils.isNotBlank(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number format
     */
    public static boolean isValidPhoneNumber(String phone) {
        return StringUtils.isNotBlank(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Check if string is not null or empty
     */
    public static boolean isNotEmpty(String str) {
        return StringUtils.isNotBlank(str);
    }

    /**
     * Validate password strength (min 8 chars, 1 uppercase, 1 lowercase, 1 number)
     */
    public static boolean isStrongPassword(String password) {
        if (StringUtils.isBlank(password) || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        return hasUpper && hasLower && hasDigit;
    }
}
