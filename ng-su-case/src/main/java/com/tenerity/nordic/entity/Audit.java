package com.tenerity.nordic.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tsu_audit")
public class Audit {
    @Id
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "uri_accessed")
    private String uriAccessed; // our URI that was accessed
    @Column(name = "remote_addr")
    private String remoteAddr; // client ip
    @Column(name = "local_ref")
    private String localRef; // ws id or document id
    @Column(name = "latest")
    private String latest; // true when access by customer/third-party
    @Column(name = "last_updated", columnDefinition = "TIMESTAMP")
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
