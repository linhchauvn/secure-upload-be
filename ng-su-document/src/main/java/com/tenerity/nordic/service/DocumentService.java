package com.tenerity.nordic.service;

import com.tenerity.nordic.client.AmazonClient;
import com.tenerity.nordic.client.dto.*;
import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.dto.DocumentDownloadResponse;
import com.tenerity.nordic.dto.DocumentListResponse;
import com.tenerity.nordic.dto.DocumentManagementRequest;
import com.tenerity.nordic.dto.DocumentManagementResponse;
import com.tenerity.nordic.dto.DocumentUploadRequest;
import com.tenerity.nordic.dto.DocumentUploadResponse;
import com.tenerity.nordic.dto.EntityResponse;
import com.tenerity.nordic.entity.Document;
import com.tenerity.nordic.enums.DocumentType;
import com.tenerity.nordic.enums.OriginatorType;
import com.tenerity.nordic.repository.DocumentRepository;
import com.tenerity.nordic.util.Constants;
import com.tenerity.nordic.util.DocumentServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    Logger logger = LoggerFactory.getLogger(InternalWebClient.class);
    @Autowired
    private AmazonClient amazonClient;
    @Autowired
    private InternalWebClient webClient;
    @Autowired
    private DocumentRepository documentRepository;

    public DocumentUploadResponse uploadDocument(DocumentUploadRequest request, MultipartFile file, String authToken) {
        DocumentUploadResponse response = validateInput(request, file);
        if (response.getCode() != null) {
            return response;
        }
        Document createdEntity = null;
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();

        Document entity = new Document();
        entity.setContentType(contentType);
        entity.setFilename(filename);
        entity.setFilePath(null);
        entity.setUploadTime(LocalDateTime.now(ZoneOffset.UTC));
        entity.setKey(DocumentType.valueOf(request.getDocumentType()));
        entity.setLabel(request.getDescription());
        entity.setCaseId(UUID.fromString(request.getCaseId()));
        if (StringUtils.isNotBlank(request.getOriginatorId())) {
            entity.setOriginatorRef(UUID.fromString(request.getOriginatorId()));
        }
        entity.setOriginatorType(OriginatorType.valueOf(request.getOriginatorType()));
        if (OriginatorType.ORIGINATOR_TYPE_AGENT == entity.getOriginatorType()) {
            entity.setMarkAsRead(Boolean.TRUE);
        } else {
            entity.setMarkAsRead(Boolean.FALSE);
        }

        createdEntity = documentRepository.save(entity);

        String fileUrl = null;

        try{
            fileUrl = uploadFile(createdEntity.getId().toString(), file);
        } catch ( Exception ex) {
            documentRepository.delete(createdEntity);
            response.setMessage("Error while uploading file. " + ex.getMessage());
            response.setCode(Constants.ERROR_CODE_AWS_ERROR);
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }

        createdEntity.setFilePath(fileUrl);

        documentRepository.save(createdEntity);

        response.setData(DocumentServiceUtils.convertDocument(createdEntity));

        if (StringUtils.isNotBlank(request.getWorkspaceId())) {
            var workspaceDocumentRequest = new WorkspaceDocumentRequest();
            workspaceDocumentRequest.setId(request.getWorkspaceId());
            workspaceDocumentRequest.setDocumentIds(Arrays.asList(createdEntity.getId().toString()));
            workspaceDocumentRequest.setCustomerDocument(!(OriginatorType.ORIGINATOR_TYPE_AGENT == entity.getOriginatorType()));
            webClient.addDocumentToWorkspace(workspaceDocumentRequest);

            if (OriginatorType.ORIGINATOR_TYPE_AGENT == createdEntity.getOriginatorType()) {
                var workspace = webClient.getWorkspaceById(request.getWorkspaceId());
                var caseDto = webClient.getCaseById(request.getCaseId());
                String agentId = DocumentServiceUtils.extractUserUUIDFromToken(authToken);
                webClient.createNotification(createUploadNotificationRequest(agentId, createdEntity.getLabel(), caseDto.getSuperOfficeId(), workspace));
            } else {
                var caseDto = webClient.getCaseById(request.getCaseId());
                String authId = DocumentServiceUtils.extractUserUUIDFromToken(authToken);
                webClient.createNotification(createNonAgentUploadNotificationRequest(authId, createdEntity.getLabel(), caseDto.getId(), caseDto.getSuperOfficeId(), caseDto.getAssignedAgent().getId()));
                webClient.triggerUploadEmail(createEmailRequest(createdEntity, caseDto));
            }
        }

        var updateTimeForCaseRequest = new CaseManagementRequest();
        updateTimeForCaseRequest.setLastUpdate(LocalDateTime.now());
        updateTimeForCaseRequest.setDocumentsExpunged(Boolean.FALSE);
        if (!entity.getMarkAsRead()) {
            updateTimeForCaseRequest.setNeedAgentNotification(Boolean.TRUE);
        }
        webClient.setDocumentActionForCase(entity.getCaseId(), updateTimeForCaseRequest);
        return response;
    }

    public DocumentListResponse getDocumentByCaseId(String caseId) {
        DocumentListResponse response = DocumentServiceUtils.validateCaseId(caseId);

        List<Document> documents = documentRepository.findAllByCaseId(UUID.fromString(caseId));
        if (!documents.isEmpty()) {
            var dtos = documents.stream().map(DocumentServiceUtils::convertDocument).collect(Collectors.toList());
            response.setData(dtos);
        }
        return response;
    }

    public DocumentListResponse getDocumentByListIds(String docIds) {
        DocumentListResponse response = new DocumentListResponse();
        if (StringUtils.isBlank(docIds)) {
            response.setData(new ArrayList());
            return response;
        }
        String[] idArray = docIds.split(Constants.COMMA_DELIMITER);
        List<UUID> listId = Arrays.stream(idArray).map(UUID::fromString).collect(Collectors.toList());
        List<Document> documents = documentRepository.findAllByIdIn(listId);
        if (!documents.isEmpty()) {
            var dtos = documents.stream().map(DocumentServiceUtils::convertDocument).collect(Collectors.toList());
            response.setData(dtos);
        }
        return response;
    }

    public DocumentManagementResponse getDocumentById(String id) {
        DocumentManagementResponse response = DocumentServiceUtils.validateUUID(id);

        var document = documentRepository.findById(UUID.fromString(id));
        if (!document.isPresent()) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find document by given id=%s", id));
            return response;
        }

        response.setData(DocumentServiceUtils.convertDocument(document.get()));
        return response;
    }

    public DocumentManagementResponse updateDocument(String id, DocumentManagementRequest request, String authToken) {
        DocumentManagementResponse response = validateUpdateDocument(id, request);
        if (StringUtils.isNotBlank(response.getCode())) {
            return response;
        }

        try {
            Document entity = documentRepository.getById(UUID.fromString(id));
            if (request.getDescription() != null) {
                String authId = DocumentServiceUtils.extractUserUUIDFromToken(authToken);
                var caseDto = webClient.getCaseById(entity.getCaseId().toString());
                if (DocumentServiceUtils.isCustomer(authToken) || isThirdPartyUser(authToken)) {
                    webClient.createNotification(createUpdateLabelNotificationRequest(authId, caseDto, null, entity.getLabel(), request.getDescription(), false));
                } else {
                    var workspaces = caseDto.getWorkspaces().stream().filter(item -> item.getDocumentIds() != null
                            && item.getDocumentIds().contains(id)).collect(Collectors.toList());
                    workspaces.forEach(ws -> {
                        webClient.createNotification(createUpdateLabelNotificationRequest(authId, caseDto, ws, entity.getLabel(), request.getDescription(), true));
                    });
                }

                entity.setLabel(request.getDescription());
            }
            if (request.getMarkAsRead() != null) {
                entity.setMarkAsRead(request.getMarkAsRead());
            }
            if (request.getNeedESignature() != null) {
                entity.setNeedESignature(request.getNeedESignature());
                if (request.getNeedESignature()) {
                    var caseDto = webClient.getCaseById(entity.getCaseId().toString());
                    String agentUUID = DocumentServiceUtils.extractUserUUIDFromToken(authToken);
                    var workspaces = caseDto.getWorkspaces().stream().filter(item -> item.getDocumentIds() != null
                            && item.getDocumentIds().contains(id) && item.getBelongToCustomer()).collect(Collectors.toList());
                    workspaces.forEach(ws -> {
                        webClient.createNotification(createESignNotificationRequest(agentUUID, entity.getLabel(), caseDto, ws.getId()));
                    });
                }
            }

            Document updatedDocument = documentRepository.save(entity);

            if (request.getMarkAsRead() != null) {
                var allDocs = documentRepository.findAllByCaseId(updatedDocument.getCaseId());
                if (allDocs.stream().allMatch(item -> item.getMarkAsRead() == null || item.getMarkAsRead())) {
                    var documentActionRequest = new CaseManagementRequest();
                    documentActionRequest.setLastUpdate(LocalDateTime.now());
                    documentActionRequest.setNeedAgentNotification(Boolean.FALSE);
                    webClient.setDocumentActionForCase(updatedDocument.getCaseId(), documentActionRequest);
                }
            }

            response.setData(DocumentServiceUtils.convertDocument(updatedDocument));
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find document by given id=%s", id));
            return response;
        }
    }

    public DocumentManagementResponse deleteDocument(String id) {
        DocumentManagementResponse response = DocumentServiceUtils.validateUUID(id);
        if (response.getHttpCode() != null) {
            return response;
        }

        try {
            Document entity = documentRepository.getById(UUID.fromString(id));
            UUID caseId = entity.getCaseId();

            webClient.deleteDocumentReferencePath(id);
            documentRepository.deleteById(UUID.fromString(id));
            amazonClient.removeObject(entity.getId().toString());

            var allDocs = documentRepository.findAllByCaseId(caseId);
            if (allDocs == null || allDocs.isEmpty()) {
                var request = new CaseManagementRequest();
                request.setLastUpdate(null);
                request.setDocumentsExpunged(Boolean.TRUE);
                request.setNeedAgentNotification(Boolean.FALSE);
                webClient.setDocumentActionForCase(caseId, request);
            }
            return response;
        } catch (Exception e) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    public DocumentManagementResponse deleteDocumentsByCase(String caseId) {
        DocumentManagementResponse response = DocumentServiceUtils.validateUUID(caseId);
        if (response.getHttpCode() != null) {
            return response;
        }

        try {
            List<Document> documents = documentRepository.findAllByCaseId(UUID.fromString(caseId));
            documents.forEach(doc -> {
                amazonClient.removeObject(doc.getId().toString());
                documentRepository.deleteById(doc.getId());
            });
            return response;
        } catch (Exception e) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_DELEtE_BY_CASE_ERROR);
            response.setMessage(String.format("Error while delete documents by caseId=%s", caseId));
            return response;
        }
    }

    public DocumentManagementResponse getDocumentContent(String id) {
        DocumentManagementResponse response = DocumentServiceUtils.validateUUID(id);

        var document = documentRepository.findById(UUID.fromString(id));
        if (!document.isPresent()) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find document by given id=%s", id));
            return response;
        }
        byte[] content = null;
        try {
            content = amazonClient.readObject(document.get().getId().toString());
        } catch (Exception ex){
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_AWS_ERROR);
            response.setMessage(String.format("Error while reading file content, id=%s", id));
            return response;
        }
        if (content == null || content.length == 0) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_FILE);
            response.setMessage(String.format("Error while reading file content, id=%s", id));
            return response;
        }

        String encode = Base64.getEncoder().encodeToString(content);
        response.setData(encode);
        return response;
    }

    public DocumentManagementResponse bulkUpdateDocument(DocumentManagementRequest request) {
        DocumentManagementResponse response = new DocumentManagementResponse();
        try {
            String[] idArray = request.getDocIds().split(Constants.COMMA_DELIMITER);
            List<UUID> listId = Arrays.stream(idArray).map(UUID::fromString).collect(Collectors.toList());
            List<Document> documents = documentRepository.findAllByIdIn(listId);
            if (!documents.isEmpty()) {
                documents.forEach(item -> {
                    if (request.getSignicatRequestId() != null) {
                        item.setSignicatRequestId(request.getSignicatRequestId());
                    }
                    if (request.getSignicatTaskId() != null) {
                        item.setSignicatTaskId(request.getSignicatTaskId());
                    }
                    if (request.geteSigned() != null) {
                        item.setESigned(request.geteSigned());
                        if (request.geteSigned()) {
                            item.setNeedESignature(Boolean.FALSE);
                            var caseDto = webClient.getCaseById(item.getCaseId().toString());
                            webClient.createNotification(createCustomerESignNotificationRequest(caseDto));
                        }
                    }
                });
            }
            documentRepository.saveAll(documents);
        } catch (Exception e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setMessage("Error while doing bulkUpdateDocument. " + e.getMessage());
        }
        return response;
    }

    public DocumentDownloadResponse downloadFile(String id, String clientIp) {
        DocumentDownloadResponse response = new DocumentDownloadResponse();

        var document = documentRepository.findById(UUID.fromString(id));
        if (!document.isPresent()) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find document by given id=%s", id));
            return response;
        }

//        var caseDto = webClient.getCaseById(document.get().getCaseId().toString());
//        if (caseDto == null || "CLOSE".equals(caseDto.getStatus())) {
//            response.setHttpCode(HttpStatus.FORBIDDEN.value());
//            response.setCode(Constants.ERROR_CODE_CASE_CLOSED);
//            response.setMessage("Case has been closed!");
//            return response;
//        }
        byte[] content = null;
        try {
            content = amazonClient.readObject(document.get().getId().toString());
        }catch (Exception ex){
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_AWS_ERROR);
            response.setMessage(String.format("Error while reading file content, id=%s", id));
            return response;
        }
        if (content == null || content.length == 0) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_FILE);
            response.setMessage(String.format("Error while reading file content, id=%s", id));
            return response;
        }
        response.setFilename(document.get().getFilename());
        MediaType contentType;
        try {
            contentType = MediaType.parseMediaType(document.get().getContentType());
        } catch (Exception e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }
        response.setContentType(contentType);
        response.setContentData(content);

        webClient.createAuditData(createAuditReq(id, clientIp));

        return response;
    }

    private AuditCreationRequest createAuditReq(String docId, String clientIp) {
        var value = ServletUriComponentsBuilder.fromCurrentRequest().build().toString();
        AuditCreationRequest auditReq = new AuditCreationRequest();
        auditReq.setUriAccessed(value);
        auditReq.setRemoteAddr(clientIp);
        auditReq.setLocalRef(docId);
        auditReq.setLatest(true);
        return auditReq;
    }

    public EntityResponse uploadSignicatFile(String id, MultipartFile file) {
        var response = new EntityResponse();
        var optional = documentRepository.findById(UUID.fromString(id));
        if (!optional.isPresent()) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find document by given id=%s", id));
            return response;
        }
        var document = optional.get();
        try {
            logger.info(String.format("[uploadSignicatFile] id=%s, size=%s", id, file.getSize()));
            var filePath = uploadFile(document.getId().toString(), file);
            document.setFilePath(filePath);
            documentRepository.save(document);
        } catch (IOException e) {
            logger.info(String.format("[uploadSignicatFile] Error %s TRACE e", e.getMessage(), e ));
            response.setMessage("Error while uploadSignicatFile file. " + e.getMessage());
            response.setCode(Constants.ERROR_CODE_INVALID_FILE);
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    private String uploadFile(String fileId, MultipartFile file) throws IOException {
        File convFile = new File(fileId);
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        String fileUrl = amazonClient.uploadObject(fileId,convFile);
        convFile.delete();
        return fileUrl;
    }

    private DocumentUploadResponse validateInput(DocumentUploadRequest request, MultipartFile file) {
        var response = new DocumentUploadResponse();
        if (StringUtils.isBlank(request.getDocumentType()) || StringUtils.isBlank(request.getDescription()) || StringUtils.isBlank(request.getCaseId())
                || StringUtils.isBlank(request.getOriginatorType()) || file == null) {
            String message = "documentType, description, caseId, originatorType and file must be provided!";
            response.setMessage(message);
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }

        try {
            DocumentType.valueOf(request.getDocumentType());
        }
        catch (IllegalArgumentException e) {
            String message = "documentType must be PROOF_OF_IDENTITY or POWER_OF_ATTORNEY or CASE_DOC.";
            response.setMessage(message);
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }

        try {
            UUID.fromString(request.getCaseId());
        } catch (NullPointerException | IllegalArgumentException e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setMessage("Invalid caseId format");
            return response;
        }

        var document = documentRepository.findDocumentByLabelAndCaseId(request.getDescription(), UUID.fromString(request.getCaseId()));
        if (document.isPresent() && request.getCaseId().equals(document.get().getCaseId().toString())) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_DUPLICATED_LABEL);
            response.setMessage("Description must be unique.");
            return response;
        }

        String filename = file.getOriginalFilename();
        document = documentRepository.findDocumentByFilenameAndCaseId(filename, UUID.fromString(request.getCaseId()));
        if (document.isPresent() && request.getCaseId().equals(document.get().getCaseId().toString())) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_DUPLICATED_FILE);
            response.setMessage(String.format("File name %s already exists.", filename));
            return response;
        }

        var caseDto = webClient.getCaseById(request.getCaseId());
        if (caseDto == null || "CLOSE".equals(caseDto.getStatus())) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_CASE_INVALID);
            response.setMessage("Case not exists or has been closed!");
            return response;
        }

        if (StringUtils.isNotBlank(request.getWorkspaceId())) {
            var workspaceDto = webClient.getWorkspaceById(request.getWorkspaceId());
            if (workspaceDto == null) {
                response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                response.setCode(Constants.ERROR_CODE_WORKSPACE_INVALID);
                response.setMessage("Workspace not exists or has been deleted!");
                return response;
            }
        }
        return response;
    }

    private DocumentManagementResponse validateUpdateDocument(String id, DocumentManagementRequest request) {
        var response = DocumentServiceUtils.validateUUID(id);
        if (response.getCode() == null) {
            try {
                var document = documentRepository.getById(UUID.fromString(id));

                var caseDto = webClient.getCaseById(document.getCaseId().toString());
                if (caseDto == null || "CLOSE".equals(caseDto.getStatus())) {
                    response.setHttpCode(HttpStatus.FORBIDDEN.value());
                    response.setCode(Constants.ERROR_CODE_CASE_CLOSED);
                    response.setMessage("Case has been closed!");
                    return response;
                }

                if (StringUtils.isNotBlank(request.getDescription())) {
                    var optionalDoc = documentRepository.findDocumentByLabelAndCaseId(request.getDescription(), document.getCaseId());
                    if (optionalDoc.isPresent()) {
                        response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                        response.setCode(Constants.ERROR_CODE_DUPLICATED_FILE);
                        response.setMessage(String.format("File name %s already exists.", request.getDescription()));
                        return response;
                    }
                }

                if (request.getNeedESignature() != null && request.getNeedESignature()) {
                    if (!MediaType.APPLICATION_PDF_VALUE.equals(document.getContentType())) {
                        response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                        response.setCode(Constants.ERROR_CODE_DOCUMENT_INVALID_FORMAT);
                        response.setMessage("Only pdf file can be request esign.");
                        return response;
                    }

                    if (document.getESigned() != null && document.getESigned()) {
                        response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                        response.setCode(Constants.ERROR_CODE_DOCUMENT_ALREADY_SIGNED);
                        response.setMessage("Document already signed.");
                        return response;
                    }

                }
            } catch (EntityNotFoundException e) {
                response.setHttpCode(HttpStatus.NOT_FOUND.value());
                response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
                response.setMessage(String.format("Cannot find document by given id=%s", id));
                return response;
            }
        }
        return response;
    }

    private UserCreateNotificationRequest createUploadNotificationRequest(String agentId, String label, String superOfficeId, WorkspaceDto workspace) {
        var request = new UserCreateNotificationRequest();
        if (workspace.getBelongToCustomer()) {
            request.setNotifyIds(Arrays.asList(workspace.getId()));
        } else {
            request.setOrganizationId(workspace.getThirdPartyId());
        }

        request.setCaseId(workspace.getCaseId().toString());
        request.setCaseName(superOfficeId);
        request.setActionType("UPLOAD");
        request.setActionObject(label);
        request.setActionAuthorId(agentId);
        return request;
    }

    private UserCreateNotificationRequest createNonAgentUploadNotificationRequest(String authId, String label,UUID caseId, String superOfficeId, UUID assignedAgent) {
        var request = new UserCreateNotificationRequest();
        request.setNotifyIds(Arrays.asList(assignedAgent));

        request.setCaseId(caseId.toString());
        request.setCaseName(superOfficeId);
        request.setActionType("UPLOAD");
        request.setActionObject(label);
        request.setActionAuthorId(authId);
        return request;
    }

    private UserCreateNotificationRequest createUpdateLabelNotificationRequest(String authId, CaseDto caseDto, WorkspaceDto workspace, String oldValue, String newValue, boolean isAuthorAgent) {
        var request = new UserCreateNotificationRequest();
        if (isAuthorAgent) {
            if (workspace.getBelongToCustomer()) {
                request.setNotifyIds(Arrays.asList(workspace.getId()));
            } else {
                request.setOrganizationId(workspace.getThirdPartyId());
            }
        } else {
            request.setNotifyIds(Arrays.asList(caseDto.getAssignedAgent().getId()));
        }
        request.setCaseId(caseDto.getId().toString());
        request.setCaseName(caseDto.getSuperOfficeId());
        request.setActionType("EDIT");
        request.setActionAuthorId(authId);
        request.setOldValue(oldValue);
        request.setNewValue(newValue);
        return request;
    }

    private UserCreateNotificationRequest createESignNotificationRequest(String authId, String label, CaseDto dto, UUID workspaceId) {
        var request = new UserCreateNotificationRequest();
        request.setNotifyIds(Arrays.asList(workspaceId));
        request.setCaseId(dto.getId().toString());
        request.setCaseName(dto.getSuperOfficeId());
        request.setActionType("REQUESTESIGN");
        request.setActionObject(label);
        request.setActionAuthorId(authId);
        return request;
    }

    private UserCreateNotificationRequest createCustomerESignNotificationRequest(CaseDto dto) {
        var request = new UserCreateNotificationRequest();
        request.setNotifyIds(Arrays.asList(dto.getAssignedAgent().getId()));
        request.setCaseId(dto.getId().toString());
        request.setCaseName(dto.getSuperOfficeId());
        request.setActionType("CUSTOMERESIGN");
        request.setActionObject("Customer Workspace");
        return request;
    }

    public boolean isThirdPartyUser(String authToken) {
        if (StringUtils.isNotBlank(authToken) && !DocumentServiceUtils.isCustomer(authToken)) {
            var tpUser = webClient.getThirdPartyUserById(DocumentServiceUtils.extractUserUUIDFromToken(authToken));
            return tpUser != null;
        }
        return false;
    }

    private DocumentEmailRequest createEmailRequest(Document doc, CaseDto caseDto){
        String agentEmail = caseDto.getAssignedAgent().getEmailAddress();
        String customerEmail = caseDto.getCustomerEmail();
        var request = new DocumentEmailRequest();
        var fileData = request.new FileData();
        fileData.setMarkasread(doc.getMarkAsRead());
        fileData.setFilename(String.format("%s [%s]", doc.getLabel(), formatDateTime(doc.getUploadTime().atOffset(ZoneOffset.UTC))));
        request.setFileData(Arrays.asList(fileData));
        request.setAgentEmail(agentEmail);
        request.setCustomerEmail(customerEmail);
        request.setCaseId(caseDto.getId().toString());
        request.setId(caseDto.getSuperOfficeId());
        request.setTemplateName("Secure_Upload_New_Upload_To_Case_EN");
        return request;
    }

    private String formatDateTime(OffsetDateTime time) {
        var pattern = new DateTimeFormatterBuilder().appendPattern("MMM dd, yyyy - hh:mm a").toFormatter();
        return time.format(pattern);
    }

//    private void replaceEsignedDocument(Document document) {
//        var packagingTaskResult = webClient.getPackagingTaskResult(document.getSignicatRequestId(), document.getId().toString());
//        if (packagingTaskResult == null || packagingTaskResult.getContent() == null) {
//            logger.error(String.format("Cannot get signed document from given requestId=%s and packagingTaskId=%s",document.getSignicatRequestId(), document.getId().toString()));
//            return;
//        }
//
//        try {
//            byte[] byteData = Base64.getDecoder().decode(packagingTaskResult.getContent());
//            File convFile = new File(document.getFilename());
//            FileOutputStream fos = new FileOutputStream(convFile);
//            fos.write(byteData);
//            fos.close();
//            String fileUrl = amazonClient.uploadObject(document.getFilename(),convFile);
//            document.setFilePath(fileUrl);
//            documentRepository.save(document);
//            convFile.delete();
//        } catch (Exception e) {
//            logger.error("[replaceEsignedDocument] ERROR: " + e.getMessage());
//        }
//    }

}
