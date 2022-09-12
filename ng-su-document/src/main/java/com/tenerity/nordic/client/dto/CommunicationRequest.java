package com.tenerity.nordic.client.dto;

import java.util.Map;

public class CommunicationRequest {

    private String toAddress;
    private Map<String, String> templateModel;

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public Map<String, String> getTemplateModel() {
        return templateModel;
    }

    public void setTemplateModel(Map<String, String> templateModel) {
        this.templateModel = templateModel;
    }
}
