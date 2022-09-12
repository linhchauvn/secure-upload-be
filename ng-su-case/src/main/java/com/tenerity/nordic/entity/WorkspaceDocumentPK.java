package com.tenerity.nordic.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class WorkspaceDocumentPK implements Serializable {
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @Column(name = "workspace_id")
    private UUID workspaceId;
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @Column(name = "document_id")
    private UUID documentId;

    public WorkspaceDocumentPK() {
    }

    public WorkspaceDocumentPK(UUID workspaceId, UUID documentId) {
        this.workspaceId = workspaceId;
        this.documentId = documentId;
    }

    public UUID getWorkspace() {
        return workspaceId;
    }

    public void setWorkspace(UUID workspaceId) {
        this.workspaceId = workspaceId;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }
}
