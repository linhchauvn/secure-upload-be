package com.tenerity.nordic.dto;

public class DocumentUploadRequest {
    private String description;
    private String documentType;
    private String caseId;
    private String workspaceId;
    private String originatorId;
    private String originatorType;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getOriginatorId() {
        return originatorId;
    }

    public void setOriginatorId(String originatorId) {
        this.originatorId = originatorId;
    }

    public String getOriginatorType() {
        return originatorType;
    }

    public void setOriginatorType(String originatorType) {
        this.originatorType = originatorType;
    }
}
