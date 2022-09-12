package com.tenerity.nordic.dto;

public class CustomerLoginRequest {
    private String workspaceId;
    private String customerToken;
    private String signicatCode;

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getCustomerToken() {
        return customerToken;
    }

    public void setCustomerToken(String customerToken) {
        this.customerToken = customerToken;
    }

    public String getSignicatCode() {
        return signicatCode;
    }

    public void setSignicatCode(String signicatCode) {
        this.signicatCode = signicatCode;
    }
}
