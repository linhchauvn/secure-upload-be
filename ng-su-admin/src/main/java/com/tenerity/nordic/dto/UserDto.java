package com.tenerity.nordic.dto;

import com.tenerity.nordic.enums.Locale;

import java.util.UUID;

public class UserDto {
    private UUID id;
    private String emailAddress;
    private Boolean isOutOfOffice;
    private Boolean isAdmin;
    private Locale locale;
    private Boolean mustChangePassword;
    private String name;
    private OrganizationDto organisation;
    private String username;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Boolean getIsOutOfOffice() {
        return isOutOfOffice;
    }

    public void setIsOutOfOffice(Boolean OOO) {
        isOutOfOffice = OOO;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Boolean getMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrganizationDto getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganizationDto organisation) {
        this.organisation = organisation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
