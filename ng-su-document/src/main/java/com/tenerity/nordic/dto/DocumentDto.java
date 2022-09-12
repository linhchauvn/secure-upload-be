package com.tenerity.nordic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tenerity.nordic.enums.DocumentType;
import com.tenerity.nordic.enums.OriginatorType;

import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentDto {
    private UUID id;
    private String contentType;
    private String filename;
    private String fileUrl;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime;
    private Boolean isESigned;
    private DocumentType documentType;
    private String label;
    private Boolean markAsRead;
    private Boolean needESignature;
    private UUID originatorRef;
    private OriginatorType originatorType;
    private String signicatRequestId;
    private String signicatTaskId;
    private UUID caseId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Boolean getESigned() {
        return isESigned;
    }

    public void setESigned(Boolean ESigned) {
        isESigned = ESigned;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getMarkAsRead() {
        return markAsRead;
    }

    public void setMarkAsRead(Boolean markAsRead) {
        this.markAsRead = markAsRead;
    }

    public Boolean getNeedESignature() {
        return needESignature;
    }

    public void setNeedESignature(Boolean needESignature) {
        this.needESignature = needESignature;
    }

    public UUID getOriginatorRef() {
        return originatorRef;
    }

    public void setOriginatorRef(UUID originatorRef) {
        this.originatorRef = originatorRef;
    }

    public OriginatorType getOriginatorType() {
        return originatorType;
    }

    public void setOriginatorType(OriginatorType originatorType) {
        this.originatorType = originatorType;
    }

    public String getSignicatRequestId() {
        return signicatRequestId;
    }

    public void setSignicatRequestId(String signicatRequestId) {
        this.signicatRequestId = signicatRequestId;
    }

    public String getSignicatTaskId() {
        return signicatTaskId;
    }

    public void setSignicatTaskId(String signicatTaskId) {
        this.signicatTaskId = signicatTaskId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }
}
