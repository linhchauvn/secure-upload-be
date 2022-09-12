package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignicatPackagingTaskStatusResponse {
    private String orderId;
    private String packagingTaskId;
    private String packagingTaskStatus;
    private String packagingTaskResultUri;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPackagingTaskId() {
        return packagingTaskId;
    }

    public void setPackagingTaskId(String packagingTaskId) {
        this.packagingTaskId = packagingTaskId;
    }

    public String getPackagingTaskStatus() {
        return packagingTaskStatus;
    }

    public void setPackagingTaskStatus(String packagingTaskStatus) {
        this.packagingTaskStatus = packagingTaskStatus;
    }

    public String getPackagingTaskResultUri() {
        return packagingTaskResultUri;
    }

    public void setPackagingTaskResultUri(String packagingTaskResultUri) {
        this.packagingTaskResultUri = packagingTaskResultUri;
    }
}
