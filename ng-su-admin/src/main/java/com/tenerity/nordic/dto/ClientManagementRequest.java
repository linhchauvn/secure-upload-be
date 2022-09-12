package com.tenerity.nordic.dto;

public class ClientManagementRequest {
    private String name;
    private String locale;
    private String brandColour;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getBrandColour() {
        return brandColour;
    }

    public void setBrandColour(String brandColour) {
        this.brandColour = brandColour;
    }
}
