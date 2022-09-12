package com.tenerity.nordic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditDto {
    private UUID id;
    private String uriAccessed;
    private String remoteAddr;
    private String localRef;
    private String latest;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
