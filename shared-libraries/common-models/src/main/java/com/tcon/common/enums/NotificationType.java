package com.tcon.common.enums;

public enum NotificationType {
    EMAIL("Email"),
    PUSH("Push Notification"),
    SMS("SMS"),
    IN_APP("In-App Notification");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
