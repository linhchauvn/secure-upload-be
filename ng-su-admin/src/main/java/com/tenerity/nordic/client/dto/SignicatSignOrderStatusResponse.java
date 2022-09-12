package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignicatSignOrderStatusResponse {
    private String orderId;
    private String taskId;
    private String taskStatus;
    private String clientReference;
    private List<SignicatSignOrderStatusDocument> documents;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getClientReference() {
        return clientReference;
    }

    public void setClientReference(String clientReference) {
        this.clientReference = clientReference;
    }

    public List<SignicatSignOrderStatusDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<SignicatSignOrderStatusDocument> documents) {
        this.documents = documents;
    }
}
