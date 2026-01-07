package com.tcon.shared_libraries.common.constants;


public class AppConstants {

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // Date/Time
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TIME_ZONE = "UTC";

    // Demo Classes
    public static final int DEFAULT_DEMO_LIMIT = 3;
    public static final int DEMO_CLASS_MIN_DURATION = 30;
    public static final int DEMO_CLASS_MAX_DURATION = 40;

    // Class Duration
    public static final int MIN_CLASS_DURATION = 30;
    public static final int MAX_CLASS_DURATION = 120;

    // Booking
    public static final int CANCELLATION_HOURS_THRESHOLD = 24;
    public static final int NO_SHOW_GRACE_PERIOD_MINUTES = 20;
    public static final int BOOKING_ADVANCE_DAYS = 7;

    // Reminders
    public static final int REMINDER_HOURS_BEFORE = 2;
    public static final int REMINDER_MINUTES_BEFORE = 15;

    // Commission
    public static final double NON_RECURRING_COMMISSION = 0.15;
    public static final double RECURRING_COMMISSION = 0.10;

    // Recording
    public static final int RECORDING_RETENTION_DAYS = 30;
    public static final String RECORDING_QUALITY = "720p";

    // Messages
    public static final int MESSAGE_RETENTION_DAYS = 30;

    // Reschedule Limit
    public static final int TEACHER_RESCHEDULE_LIMIT = 5;
    public static final int TEACHER_NO_SHOW_PENALTY_THRESHOLD = 3;

    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }
}
