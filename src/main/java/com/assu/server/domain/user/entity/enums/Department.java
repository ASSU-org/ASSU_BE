package com.assu.server.domain.user.entity.enums;

public enum Department {
    IT("IT대학");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
