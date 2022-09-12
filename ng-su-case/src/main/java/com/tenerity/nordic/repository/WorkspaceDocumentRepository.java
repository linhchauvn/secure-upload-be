package com.tenerity.nordic.repository;

import com.tenerity.nordic.entity.WorkspaceDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface WorkspaceDocumentRepository extends JpaRepository<WorkspaceDocument, UUID> {

    @Query("select c from WorkspaceDocument c where c.id.workspaceId = :workspaceId")
    List<WorkspaceDocument> findAllByWorkspaceId(UUID workspaceId);
    @Transactional
    @Modifying
    @Query("delete from WorkspaceDocument c where c.id.workspaceId = :workspaceId and c.id.documentId = :documentId")
    void deleteByWorkspaceIdAndDocumentId(UUID workspaceId, UUID documentId);
    @Transactional
    @Modifying
    @Query("delete from WorkspaceDocument c where c.id.documentId = :documentId")
    void deleteByDocumentId(UUID documentId);
}
