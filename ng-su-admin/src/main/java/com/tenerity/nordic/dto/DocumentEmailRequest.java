package com.tenerity.nordic.dto;

import java.util.List;

public class DocumentEmailRequest {
    private String customerEmail;
    private String agentEmail;
    private List<DocumentEmailFileData> fileData;
    private String caseId;
    private String id;
    private String templateName;

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }


    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<DocumentEmailFileData> getFileData() {
        return fileData;
    }

    public void setFileData(List<DocumentEmailFileData> fileData) {
        this.fileData = fileData;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
