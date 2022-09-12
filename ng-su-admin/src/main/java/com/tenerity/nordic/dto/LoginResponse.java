package com.tenerity.nordic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude (JsonInclude.Include.NON_EMPTY)
public class LoginResponse extends EntityResponse {
    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
