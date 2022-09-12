package com.tenerity.nordic.client.dto;

public class AuditCreationRequest {
    private String uriAccessed; // resource url
    private String remoteAddr; // client ip
    private String localRef; // workspace/document id
    private Boolean latest; // true if access by third-party/customer

    public String getUriAccessed() {
        return uriAccessed;
    }

    public void setUriAccessed(String uriAccessed) {
        this.uriAccessed = uriAccessed;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getLocalRef() {
        return localRef;
    }

    public void setLocalRef(String localRef) {
        this.localRef = localRef;
    }

    public Boolean getLatest() {
        return latest;
    }

    public void setLatest(Boolean latest) {
        this.latest = latest;
    }
}
