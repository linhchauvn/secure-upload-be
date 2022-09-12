package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NgAuthLoginResponse {
    private String memberId;
    private String scope;
    private String lastLoginTime;
    @JsonProperty(value = "access_token")
    private String accessToken;
    @JsonProperty(value = "expires_in")
    private Long expiresIn;
    @JsonProperty(value = "refresh_expires_in")
    private Long refreshExpiresIn;
    @JsonProperty(value = "refresh_token")
    private String refreshToken;
    @JsonProperty(value = "token_type")
    private String tokenType;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    public void setRefreshExpiresIn(Long refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
