package com.tenerity.nordic.dto;

public class UserManagementRequest {
    private String username;
    private String password;
    private String emailAddress;
    private Boolean isAdmin;
    private Boolean isOutOfOffice;
    private String locale;
    private String organizationId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getIsOutOfOffice() {
        return isOutOfOffice;
    }

    public void setIsOutOfOffice(Boolean outOfOffice) {
        isOutOfOffice = outOfOffice;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
