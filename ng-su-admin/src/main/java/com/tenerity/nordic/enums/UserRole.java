package com.tenerity.nordic.enums;

public enum UserRole {
    ADMIN("admin"),
    AGENT("agent"),
    THIRDPARTY("third-party");

    private String val;

    private UserRole(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
