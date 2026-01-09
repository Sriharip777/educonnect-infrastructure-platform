package com.tcon.common.enums;

public enum UserRole {
    STUDENT("Student"),
    TEACHER("Teacher"),
    PARENT("Parent"),
    ADMIN("Admin"),
    SUPPORT_STAFF("Support Staff"),
    FINANCE_ADMIN("Finance Admin");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
