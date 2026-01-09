package com.tcon.common.constants;

public final class AppConstants {

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // Date/Time Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TIME_ZONE = "UTC";

    // Demo Classes
    public static final int DEFAULT_DEMO_LIMIT = 3;
    public static final int DEMO_CLASS_MIN_DURATION = 30; // minutes
    public static final int DEMO_CLASS_MAX_DURATION = 40; // minutes

    // Class Duration
    public static final int MIN_CLASS_DURATION = 30;  // minutes
    public static final int MAX_CLASS_DURATION = 120; // minutes

    // Booking
    public static final int CANCELLATION_HOURS_THRESHOLD = 24;
    public static final int NO_SHOW_GRACE_PERIOD_MINUTES = 20;
    public static final int BOOKING_ADVANCE_DAYS = 7;

    // Reminders
    public static final int REMINDER_HOURS_BEFORE = 2;
    public static final int REMINDER_MINUTES_BEFORE = 15;

    // Commission Rates
    public static final double NON_RECURRING_COMMISSION = 0.15; // 15%
    public static final double RECURRING_COMMISSION = 0.10;     // 10%

    // Recording
    public static final int RECORDING_RETENTION_DAYS = 30;
    public static final String RECORDING_QUALITY = "720p";

    // Messages
    public static final int MESSAGE_RETENTION_DAYS = 30;

    // Teacher Limits
    public static final int TEACHER_RESCHEDULE_LIMIT = 5;
    public static final int TEACHER_NO_SHOW_PENALTY_THRESHOLD = 3;

    // Validation Messages
    public static final String VALIDATION_EMAIL = "Invalid email format";
    public static final String VALIDATION_PHONE = "Invalid phone number";
    public static final String VALIDATION_REQUIRED = "This field is required";

    // Error Codes
    public static final String ERROR_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String ERROR_NOT_FOUND = "NOT_FOUND";
    public static final String ERROR_VALIDATION = "VALIDATION_ERROR";
    public static final String ERROR_INTERNAL = "INTERNAL_SERVER_ERROR";

    private AppConstants() {
        throw new IllegalStateException("Utility class - cannot be instantiated");
    }
}
