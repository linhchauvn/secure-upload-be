package com.tenerity.nordic.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tenerity.nordic.dto.EntityResponse;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AdminPanelManagementResponse<T> extends EntityResponse {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
