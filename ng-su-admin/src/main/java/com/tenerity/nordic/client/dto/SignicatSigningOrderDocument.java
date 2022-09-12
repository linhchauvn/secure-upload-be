package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignicatSigningOrderDocument {
    private String id;
    private String description;
    private String action;
    private String source;
    private String documentRef;
    private Boolean sendResultToArchive;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDocumentRef() {
        return documentRef;
    }

    public void setDocumentRef(String documentRef) {
        this.documentRef = documentRef;
    }

    public Boolean getSendResultToArchive() {
        return sendResultToArchive;
    }

    public void setSendResultToArchive(Boolean sendResultToArchive) {
        this.sendResultToArchive = sendResultToArchive;
    }
}
