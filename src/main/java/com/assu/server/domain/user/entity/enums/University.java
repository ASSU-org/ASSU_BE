package com.assu.server.domain.user.entity.enums;

public enum University {
    SSU("숭실대학교");

    private final String displayName;

    University(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
