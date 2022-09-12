package com.tenerity.nordic.dto;

public class DocumentEsignedDownloadResponse extends EntityResponse {
    private String token;
    private String resultUrl;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }
}
