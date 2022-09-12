package com.tenerity.nordic.client.dto;

import org.springframework.beans.factory.annotation.Value;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class CommunicationRequest {


    private String tenantId;

    private String applicationId;

    private String correlationId;

    private String timeStamp;

    private MessageConfiguration messageConfiguration;

    public CommunicationRequest(String applicationId,String tenantId, MessageConfiguration messageConfiguration) {
        this.applicationId = applicationId;
        this.tenantId = tenantId;
        this.correlationId = UUID.randomUUID().toString();
        this.timeStamp = this.getCurrentTimeStamp();
        this.messageConfiguration = messageConfiguration;
    }

    private String getCurrentTimeStamp(){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        // HH:mm:ss.sssZ
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public MessageConfiguration getMessageConfiguration() {
        return messageConfiguration;
    }

    public void setMessageConfiguration(MessageConfiguration messageConfiguration) {
        this.messageConfiguration = messageConfiguration;
    }
}
