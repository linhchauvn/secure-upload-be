package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class CaseManagementRequest {
    private String superOfficeId;
    private String customerEmail;
    private String customerNationalId;
    private String agentId;
    private String clientId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdate;
    private Boolean documentsExpunged;
    private Boolean needAgentNotification;

    public String getSuperOfficeId() {
        return superOfficeId;
    }

    public void setSuperOfficeId(String superOfficeId) {
        this.superOfficeId = superOfficeId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerNationalId() {
        return customerNationalId;
    }

    public void setCustomerNationalId(String customerNationalId) {
        this.customerNationalId = customerNationalId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Boolean getDocumentsExpunged() {
        return documentsExpunged;
    }

    public void setDocumentsExpunged(Boolean documentsExpunged) {
        this.documentsExpunged = documentsExpunged;
    }

    public Boolean getNeedAgentNotification() {
        return needAgentNotification;
    }

    public void setNeedAgentNotification(Boolean needAgentNotification) {
        this.needAgentNotification = needAgentNotification;
    }
}
