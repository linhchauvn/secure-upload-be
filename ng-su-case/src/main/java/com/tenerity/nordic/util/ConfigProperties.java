package com.tenerity.nordic.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "java")
public class ConfigProperties {
    private String adminServiceBaseUrl;
    private String documentServiceBaseUrl;
    private String allAgentsApiPath;
    private String allClientsApiPath;
    private String allThirdPartiesApiPath;
    private String getAgentByIdApiPath;
    private String getThirdPartyUserByIdApiPath;
    private String getClientByIdApiPath;
    private String getOrganizationByIdApiPath;
    private String getDocumentListPath;
    private String getDocumentByCasePath;
    private String deleteDocumentByCasePath;
    private String createNotificationPath;
    private String triggerUploadDocumentEmail;
    private String triggerWorkspaceDeletionEmail;
    private String workspaceUrl;
    private String caseUrl;

    public String getTriggerWorkspaceDeletionEmail() {
        return triggerWorkspaceDeletionEmail;
    }

    public void setTriggerWorkspaceDeletionEmail(String triggerWorkspaceDeletionEmail) {
        this.triggerWorkspaceDeletionEmail = triggerWorkspaceDeletionEmail;
    }

    public String getWorkspaceUrl() {
        return workspaceUrl;
    }

    public void setWorkspaceUrl(String workspaceUrl) {
        this.workspaceUrl = workspaceUrl;
    }

    public String getCaseUrl() {
        return caseUrl;
    }

    public void setCaseUrl(String caseUrl) {
        this.caseUrl = caseUrl;
    }

    public String getTriggerUploadDocumentEmail() {
        return triggerUploadDocumentEmail;
    }

    public void setTriggerUploadDocumentEmail(String triggerUploadDocumentEmail) {
        this.triggerUploadDocumentEmail = triggerUploadDocumentEmail;
    }

    public String getAdminServiceBaseUrl() {
        return adminServiceBaseUrl;
    }

    public void setAdminServiceBaseUrl(String adminServiceBaseUrl) {
        this.adminServiceBaseUrl = adminServiceBaseUrl;
    }

    public String getDocumentServiceBaseUrl() {
        return documentServiceBaseUrl;
    }

    public void setDocumentServiceBaseUrl(String documentServiceBaseUrl) {
        this.documentServiceBaseUrl = documentServiceBaseUrl;
    }

    public String getAllAgentsApiPath() {
        return allAgentsApiPath;
    }

    public void setAllAgentsApiPath(String allAgentsApiPath) {
        this.allAgentsApiPath = allAgentsApiPath;
    }

    public String getAllClientsApiPath() {
        return allClientsApiPath;
    }

    public void setAllClientsApiPath(String allClientsApiPath) {
        this.allClientsApiPath = allClientsApiPath;
    }

    public String getAllThirdPartiesApiPath() {
        return allThirdPartiesApiPath;
    }

    public void setAllThirdPartiesApiPath(String allThirdPartiesApiPath) {
        this.allThirdPartiesApiPath = allThirdPartiesApiPath;
    }

    public String getGetAgentByIdApiPath() {
        return getAgentByIdApiPath;
    }

    public void setGetAgentByIdApiPath(String getAgentByIdApiPath) {
        this.getAgentByIdApiPath = getAgentByIdApiPath;
    }

    public String getGetThirdPartyUserByIdApiPath() {
        return getThirdPartyUserByIdApiPath;
    }

    public void setGetThirdPartyUserByIdApiPath(String getThirdPartyUserByIdApiPath) {
        this.getThirdPartyUserByIdApiPath = getThirdPartyUserByIdApiPath;
    }

    public String getGetClientByIdApiPath() {
        return getClientByIdApiPath;
    }

    public void setGetClientByIdApiPath(String getClientByIdApiPath) {
        this.getClientByIdApiPath = getClientByIdApiPath;
    }

    public String getGetOrganizationByIdApiPath() {
        return getOrganizationByIdApiPath;
    }

    public void setGetOrganizationByIdApiPath(String getOrganizationByIdApiPath) {
        this.getOrganizationByIdApiPath = getOrganizationByIdApiPath;
    }

    public String getGetDocumentListPath() {
        return getDocumentListPath;
    }

    public void setGetDocumentListPath(String getDocumentListPath) {
        this.getDocumentListPath = getDocumentListPath;
    }

    public String getGetDocumentByCasePath() {
        return getDocumentByCasePath;
    }

    public void setGetDocumentByCasePath(String getDocumentByCasePath) {
        this.getDocumentByCasePath = getDocumentByCasePath;
    }

    public String getDeleteDocumentByCasePath() {
        return deleteDocumentByCasePath;
    }

    public void setDeleteDocumentByCasePath(String deleteDocumentByCasePath) {
        this.deleteDocumentByCasePath = deleteDocumentByCasePath;
    }

    public String getCreateNotificationPath() {
        return createNotificationPath;
    }

    public void setCreateNotificationPath(String createNotificationPath) {
        this.createNotificationPath = createNotificationPath;
    }
}
