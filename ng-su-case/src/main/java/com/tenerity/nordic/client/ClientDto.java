package com.tenerity.nordic.client;

import com.tenerity.nordic.enums.Locale;

import java.util.UUID;

public class ClientDto {
    private UUID id;
    private String name;
    private Locale locale;
    private String brandColour;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getBrandColour() {
        return brandColour;
    }

    public void setBrandColour(String brandColour) {
        this.brandColour = brandColour;
    }
}
