package com.tenerity.nordic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginUserInfoResponse extends EntityResponse {
    private String role;
    private UserDto data;
    private String workspaceId;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserDto getData() {
        return data;
    }

    public void setData(UserDto data) {
        this.data = data;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }
}
