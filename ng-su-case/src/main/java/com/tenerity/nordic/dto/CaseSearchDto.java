package com.tenerity.nordic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.UUID;

public class CaseSearchDto {
    private UUID id;
    private String superOfficeID;
    private String assignedAgent;
    private Boolean agentOoo;
    private String client;
    private String customerEmail;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;
    private String status;

    @JsonIgnore
    private int importancePoint;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSuperOfficeID() {
        return superOfficeID;
    }

    public void setSuperOfficeID(String superOfficeID) {
        this.superOfficeID = superOfficeID;
    }

    public String getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(String assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    public Boolean getAgentOoo() {
        return agentOoo;
    }

    public void setAgentOoo(Boolean agentOoo) {
        this.agentOoo = agentOoo;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getImportancePoint() {
        return importancePoint;
    }

    public void setImportancePoint(int importancePoint) {
        this.importancePoint = importancePoint;
    }
}
