package com.tenerity.nordic.dto;

public class UserNotificationObjectDto {
    private String oldDocumentTitle;
    private String newDocumentTitle;
    private String agentName;
    private String customer;
    private String thirdparty;
    private String documentName;
    private String thirdpartyWorkspaceName;
    private String customerWorkspaceName;

    public String getOldDocumentTitle() {
        return oldDocumentTitle;
    }

    public void setOldDocumentTitle(String oldDocumentTitle) {
        this.oldDocumentTitle = oldDocumentTitle;
    }

    public String getNewDocumentTitle() {
        return newDocumentTitle;
    }

    public void setNewDocumentTitle(String newDocumentTitle) {
        this.newDocumentTitle = newDocumentTitle;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getThirdparty() {
        return thirdparty;
    }

    public void setThirdparty(String thirdparty) {
        this.thirdparty = thirdparty;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getThirdpartyWorkspaceName() {
        return thirdpartyWorkspaceName;
    }

    public void setThirdpartyWorkspaceName(String thirdpartyWorkspaceName) {
        this.thirdpartyWorkspaceName = thirdpartyWorkspaceName;
    }

    public String getCustomerWorkspaceName() {
        return customerWorkspaceName;
    }

    public void setCustomerWorkspaceName(String customerWorkspaceName) {
        this.customerWorkspaceName = customerWorkspaceName;
    }
}
