package com.tenerity.nordic.repository;

import com.tenerity.nordic.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Optional<Document> findDocumentByFilenameAndCaseId(String filename, UUID caseId);
    Optional<Document> findDocumentByLabelAndCaseId(String description, UUID caseId);
    List<Document> findAllByCaseId(UUID caseId);
    List<Document> findAllByIdIn(List<UUID> ids);
}
