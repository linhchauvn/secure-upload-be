package com.tenerity.nordic.client.dto;

import java.util.List;

public class WorkspaceDocumentRequest {
    private String id;
    private List<String> documentIds;
    private boolean customerDocument;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<String> documentIds) {
        this.documentIds = documentIds;
    }

    public boolean isCustomerDocument() {
        return customerDocument;
    }

    public void setCustomerDocument(boolean customerDocument) {
        this.customerDocument = customerDocument;
    }
}
