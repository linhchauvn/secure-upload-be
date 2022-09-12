package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignicatSignOrderStatusDocument {
    private String id;
    private String originalUri;
    private String resultUri;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public void setOriginalUri(String originalUri) {
        this.originalUri = originalUri;
    }

    public String getResultUri() {
        return resultUri;
    }

    public void setResultUri(String resultUri) {
        this.resultUri = resultUri;
    }
}
