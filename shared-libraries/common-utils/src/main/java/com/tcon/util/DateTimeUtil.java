package com.tcon.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations
 */
public class DateTimeUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");

    /**
     * Get current date time in UTC
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE);
    }

    /**
     * Get current date in UTC
     */
    public static LocalDate today() {
        return LocalDate.now(DEFAULT_ZONE);
    }

    /**
     * Format LocalDateTime to string
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * Format LocalDate to string
     */
    public static String format(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Format LocalTime to string
     */
    public static String format(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    /**
     * Parse string to LocalDateTime
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    }

    /**
     * Parse string to LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * Parse string to LocalTime
     */
    public static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }

    /**
     * Add days to date
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, long days) {
        return dateTime.plusDays(days);
    }

    /**
     * Add hours to date time
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        return dateTime.plusHours(hours);
    }

    /**
     * Add minutes to date time
     */
    public static LocalDateTime addMinutes(LocalDateTime dateTime, long minutes) {
        return dateTime.plusMinutes(minutes);
    }

    /**
     * Subtract days from date
     */
    public static LocalDateTime subtractDays(LocalDateTime dateTime, long days) {
        return dateTime.minusDays(days);
    }

    /**
     * Subtract hours from date time
     */
    public static LocalDateTime subtractHours(LocalDateTime dateTime, long hours) {
        return dateTime.minusHours(hours);
    }

    /**
     * Calculate difference in days
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Calculate difference in hours
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Calculate difference in minutes
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Check if date is in the past
     */
    public static boolean isInPast(LocalDateTime dateTime) {
        return dateTime.isBefore(now());
    }

    /**
     * Check if date is in the future
     */
    public static boolean isInFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(now());
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(LocalDate date) {
        return date.isEqual(today());
    }

    /**
     * Get start of day
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * Get end of day
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

    /**
     * Convert to timestamp (epoch milliseconds)
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(DEFAULT_ZONE).toInstant().toEpochMilli();
    }

    /**
     * Convert from timestamp to LocalDateTime
     */
    public static LocalDateTime fromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), DEFAULT_ZONE);
    }

    /**
     * Check if time is within range
     */
    public static boolean isWithinRange(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return !target.isBefore(start) && !target.isAfter(end);
    }

    private DateTimeUtil() {
        throw new IllegalStateException("Utility class");
    }
}
