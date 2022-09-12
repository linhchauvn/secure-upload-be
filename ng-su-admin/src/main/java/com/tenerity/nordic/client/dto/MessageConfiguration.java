package com.tenerity.nordic.client.dto;

import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class MessageConfiguration {
    private String senderIdentity;
    private String commType = "email";
    private String receiverIdentity;
    private String templateName;
    private Map<String, Object> mergeFields;
    public MessageConfiguration(String senderIdentity, String receiverIdentity, String templateName, Map<String, Object> mergeFields) {
        this.senderIdentity = senderIdentity;
        this.receiverIdentity = receiverIdentity;
        this.templateName = templateName;
        this.mergeFields = mergeFields;
    }

    public String getSenderIdentity() {
        return senderIdentity;
    }

    public void setSenderIdentity(String senderIdentity) {
        this.senderIdentity = senderIdentity;
    }

    public String getCommType() {
        return commType;
    }

    public void setCommType(String commType) {
        this.commType = commType;
    }

    public String getReceiverIdentity() {
        return receiverIdentity;
    }

    public void setReceiverIdentity(String receiverIdentity) {
        this.receiverIdentity = receiverIdentity;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Map<String, Object> getMergeFields() {
        return mergeFields;
    }

    public void setMergeFields(Map<String, Object> mergeFields) {
        this.mergeFields = mergeFields;
    }
}
