package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignicatSigningOrderResponse {
    private String id;
    private String clientReference;
    private List<SignicatSigningOrderTask> tasks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
}
