package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignicatSigningOrderMethod {
    private String name;
    private String type;
    private Boolean handwritten;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getHandwritten() {
        return handwritten;
    }

    public void setHandwritten(Boolean handwritten) {
        this.handwritten = handwritten;
    }
}
