package com.tenerity.nordic.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tenerity.nordic.dto.EntityResponse;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentListResponse<T> extends EntityResponse {
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
