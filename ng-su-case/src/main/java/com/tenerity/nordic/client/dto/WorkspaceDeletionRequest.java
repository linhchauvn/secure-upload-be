package com.tenerity.nordic.client.dto;

public class WorkspaceDeletionRequest {
    private String workspaceLabel;
    private String workspaceUrl;
    private String customerEmail;
    private String caseUrl;
    private String agentEmail;
    private String organizationName;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public String getWorkspaceLabel() {
        return workspaceLabel;
    }

    public void setWorkspaceLabel(String workspaceLabel) {
        this.workspaceLabel = workspaceLabel;
    }

    public String getWorkspaceUrl() {
        return workspaceUrl;
    }

    public void setWorkspaceUrl(String workspaceUrl) {
        this.workspaceUrl = workspaceUrl;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCaseUrl() {
        return caseUrl;
    }

    public void setCaseUrl(String caseUrl) {
        this.caseUrl = caseUrl;
    }
}
