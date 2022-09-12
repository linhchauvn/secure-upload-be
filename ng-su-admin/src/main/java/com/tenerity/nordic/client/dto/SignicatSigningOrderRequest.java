package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignicatSigningOrderRequest {
    private String clientReference;
    private List<SignicatSigningOrderTask> tasks;
    private List<SignicatPackagingTask> packagingTasks;

    public String getClientReference() {
        return clientReference;
    }

    public void setClientReference(String clientReference) {
        this.clientReference = clientReference;
    }

    public List<SignicatSigningOrderTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<SignicatSigningOrderTask> tasks) {
        this.tasks = tasks;
    }

    public List<SignicatPackagingTask> getPackagingTasks() {
        return packagingTasks;
    }

    public void setPackagingTasks(List<SignicatPackagingTask> packagingTasks) {
        this.packagingTasks = packagingTasks;
    }
}
