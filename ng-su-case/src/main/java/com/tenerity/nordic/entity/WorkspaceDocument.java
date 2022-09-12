package com.tenerity.nordic.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "tsu_workspace_document")
public class WorkspaceDocument {
    @EmbeddedId
    private WorkspaceDocumentPK id;

    public WorkspaceDocumentPK getId() {
        return id;
    }

    public void setId(WorkspaceDocumentPK id) {
        this.id = id;
    }
}
