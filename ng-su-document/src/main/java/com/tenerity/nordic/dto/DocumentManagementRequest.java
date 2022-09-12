package com.tenerity.nordic.dto;

public class DocumentManagementRequest {
    private String description;
    private Boolean markAsRead;
    private Boolean needESignature;

    // for bulk update, signicat flow
    private String docIds;
    private String signicatRequestId;
    private String signicatTaskId;
    private Boolean eSigned;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getMarkAsRead() {
        return markAsRead;
    }

    public void setMarkAsRead(Boolean markAsRead) {
        this.markAsRead = markAsRead;
    }

    public Boolean getNeedESignature() {
        return needESignature;
    }

    public void setNeedESignature(Boolean needESignature) {
        this.needESignature = needESignature;
    }

    public String getDocIds() {
        return docIds;
    }

    public void setDocIds(String docIds) {
        this.docIds = docIds;
    }

    public String getSignicatRequestId() {
        return signicatRequestId;
    }

    public void setSignicatRequestId(String signicatRequestId) {
        this.signicatRequestId = signicatRequestId;
    }

    public String getSignicatTaskId() {
        return signicatTaskId;
    }

    public void setSignicatTaskId(String signicatTaskId) {
        this.signicatTaskId = signicatTaskId;
    }

    public Boolean geteSigned() {
        return eSigned;
    }

    public void seteSigned(Boolean eSigned) {
        this.eSigned = eSigned;
    }
}
