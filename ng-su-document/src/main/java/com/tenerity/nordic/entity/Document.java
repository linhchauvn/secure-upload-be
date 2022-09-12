package com.tenerity.nordic.entity;

import com.tenerity.nordic.converter.DocumentTypeConverter;
import com.tenerity.nordic.converter.OriginatorTypeConverter;
import com.tenerity.nordic.enums.DocumentType;
import com.tenerity.nordic.enums.OriginatorType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tsu_document")
public class Document {
    @Id
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "content_type")
    private String contentType;
    @Column(name = "filename", unique = true)
    private String filename;
    @Column(name = "is_e_signed")
    private Boolean isESigned;
    @Column(name = "key")
    @Convert(converter = DocumentTypeConverter.class)
    private DocumentType key;
    @Column(name = "label")
    private String label;
    @Column(name = "mark_as_read")
    private Boolean markAsRead;
    @Column(name = "needs_e_signature")
    private Boolean needESignature;
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @Column(name = "originator_ref")
    private UUID originatorRef;
    @Column(name = "originator_type")
    @Convert(converter = OriginatorTypeConverter.class)
    private OriginatorType originatorType;
    @Column(name = "signicat_request_id")
    private String signicatRequestId;
    @Column(name = "signicat_task_id")
    private String signicatTaskId;
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @Column(name = "case_id")
    private UUID caseId;
    @Column(name = "file_path")
    private String filePath;
    @Column(name = "upload_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime uploadTime;

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

    public Boolean getESigned() {
        return isESigned;
    }

    public void setESigned(Boolean ESigned) {
        isESigned = ESigned;
    }

    public DocumentType getKey() {
        return key;
    }

    public void setKey(DocumentType key) {
        this.key = key;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
}
