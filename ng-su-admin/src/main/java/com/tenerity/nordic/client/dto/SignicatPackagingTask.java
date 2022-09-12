package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignicatPackagingTask {
    private String id;
    private Boolean sendToArchive;
    private String method; //pades
    private List<SignicatPackagingTaskDocument> documents;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getSendToArchive() {
        return sendToArchive;
    }

    public void setSendToArchive(Boolean sendToArchive) {
        this.sendToArchive = sendToArchive;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<SignicatPackagingTaskDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<SignicatPackagingTaskDocument> documents) {
        this.documents = documents;
    }
}
