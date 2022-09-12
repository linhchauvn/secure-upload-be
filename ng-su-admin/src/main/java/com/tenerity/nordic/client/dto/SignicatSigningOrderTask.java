package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignicatSigningOrderTask {
    private String id;
    private String language;
    private List<SignicatSigningOrderDocument> documents;
    private List<SignicatSigningOrderMethod> signatureMethods;
    private String onTaskComplete;
    private String onTaskReject;
    private String signingUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<SignicatSigningOrderDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<SignicatSigningOrderDocument> documents) {
        this.documents = documents;
    }

    public List<SignicatSigningOrderMethod> getSignatureMethods() {
        return signatureMethods;
    }

    public void setSignatureMethods(List<SignicatSigningOrderMethod> signatureMethods) {
        this.signatureMethods = signatureMethods;
    }

    public String getOnTaskComplete() {
        return onTaskComplete;
    }

    public void setOnTaskComplete(String onTaskComplete) {
        this.onTaskComplete = onTaskComplete;
    }

    public String getOnTaskReject() {
        return onTaskReject;
    }

    public void setOnTaskReject(String onTaskReject) {
        this.onTaskReject = onTaskReject;
    }

    public String getSigningUrl() {
        return signingUrl;
    }

    public void setSigningUrl(String signingUrl) {
        this.signingUrl = signingUrl;
    }
}
