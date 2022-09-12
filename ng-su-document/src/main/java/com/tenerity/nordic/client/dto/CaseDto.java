package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CaseDto {
    private UUID id;
    private String superOfficeId;
    private UserDto assignedAgent;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;
    private UUID clientId;
    private ClientDto client;
    private String customerEmail;
    private String code;
    private String customerDOB;
    private String customerFirstName;
    private String customerLastName;
    private String customerNationalId;
    private String customerTokenHash;
    private Boolean documentsExpunged;
    private Boolean needAgentNotification;
    private String status;
    private List<WorkspaceDto> workspaces;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSuperOfficeId() {
        return superOfficeId;
    }

    public void setSuperOfficeId(String superOfficeId) {
        this.superOfficeId = superOfficeId;
    }

    public UserDto getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(UserDto assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public ClientDto getClient() {
        return client;
    }

    public void setClient(ClientDto client) {
        this.client = client;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCustomerDOB() {
        return customerDOB;
    }

    public void setCustomerDOB(String customerDOB) {
        this.customerDOB = customerDOB;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerNationalId() {
        return customerNationalId;
    }

    public void setCustomerNationalId(String customerNationalId) {
        this.customerNationalId = customerNationalId;
    }

    public String getCustomerTokenHash() {
        return customerTokenHash;
    }

    public void setCustomerTokenHash(String customerTokenHash) {
        this.customerTokenHash = customerTokenHash;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<WorkspaceDto> getWorkspaces() {
        return workspaces;
    }

    public void setWorkspaces(List<WorkspaceDto> workspaces) {
        this.workspaces = workspaces;
    }
}
