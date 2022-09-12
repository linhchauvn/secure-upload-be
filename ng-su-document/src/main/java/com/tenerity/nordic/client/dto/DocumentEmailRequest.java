package com.tenerity.nordic.client.dto;

import java.util.List;

public class DocumentEmailRequest {
    private String customerEmail;
    private String agentEmail;
    private List<FileData> fileData;
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

    public List<FileData> getFileData() {
        return fileData;
    }

    public void setFileData(List<FileData> fileData) {
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

    public class FileData {
        private Boolean markasread;
        private String filename;

        public Boolean getMarkasread() {
            return markasread;
        }

        public void setMarkasread(Boolean markasread) {
            this.markasread = markasread;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }

}
