package com.tenerity.nordic.service;

import com.tenerity.nordic.client.AmazonClient;
import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.dto.CaseDto;
import com.tenerity.nordic.client.dto.CaseManagementRequest;
import com.tenerity.nordic.client.dto.DocumentEmailRequest;
import com.tenerity.nordic.client.dto.UserCreateNotificationRequest;
import com.tenerity.nordic.client.dto.UserDto;
import com.tenerity.nordic.client.dto.WorkspaceDocumentRequest;
import com.tenerity.nordic.client.dto.WorkspaceDto;
import com.tenerity.nordic.dto.DocumentDto;
import com.tenerity.nordic.dto.DocumentManagementRequest;
import com.tenerity.nordic.dto.DocumentUploadRequest;
import com.tenerity.nordic.entity.Document;
import com.tenerity.nordic.enums.DocumentType;
import com.tenerity.nordic.enums.OriginatorType;
import com.tenerity.nordic.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {
    @Mock
    private AmazonClient amazonClient;
    @Mock
    private InternalWebClient webClient;
    @Mock
    private DocumentRepository documentRepository;
    @InjectMocks
    private DocumentService documentService;

    @Test
    void uploadDocument_happyCase() {
        var token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                "eyJtZW1iZXJfaWQiOiIzODczMWE3MC01ZjMxLTRkOGEtYjhhMi0yMmQ1ZWFkODBhYzQiLCJzdWIiOiJzdGF0aWMtand0IiwiaXNzIjoic2VjdXJlLnVwbG9hZCI" +
                "sInByZWZlcnJlZF91c2VybmFtZSI6IkNVU1RPTUVSIiwiZXhwIjoxNjQyMzY3ODIwfQ." +
                "vmHGQA5Vkn039lHTs78u4VaqYn9tt56J3Z_ZX_H-jXA";
        var wsId = "38731a70-5f31-4d8a-b8a2-22d5ead80ac4";
        MockMultipartFile file = new MockMultipartFile("filename", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        var caseId = UUID.randomUUID();
        var doc = new Document();
        doc.setId(UUID.randomUUID());
        doc.setContentType(MediaType.TEXT_PLAIN_VALUE);
        doc.setFilename("filename");
        doc.setFilePath("fileUrl");
        doc.setUploadTime(LocalDateTime.now());
        doc.setKey(DocumentType.CASE_DOC);
        doc.setLabel("testupload");
        doc.setCaseId(caseId);
        doc.setOriginatorType(OriginatorType.ORIGINATOR_TYPE_CUSTOMER);
        doc.setMarkAsRead(Boolean.FALSE);
        when(documentRepository.save(any())).thenReturn(doc);
        when(webClient.getWorkspaceById(wsId)).thenReturn(new WorkspaceDto());
        when(amazonClient.uploadObject(any(), any())).thenReturn("fileUrl");

        var caseDto = new CaseDto();
        caseDto.setId(caseId);
        caseDto.setSuperOfficeId("superOfficeId");
        var agent = new UserDto();
        agent.setId(UUID.randomUUID());
        agent.setEmailAddress("agent@email.com");
        caseDto.setAssignedAgent(agent);
        caseDto.setCustomerEmail("customer@email.com");
        when(webClient.getCaseById(caseId.toString())).thenReturn(caseDto);

        var req = new DocumentUploadRequest();
        req.setCaseId(caseId.toString());
        req.setDocumentType("CASE_DOC");
        req.setDescription("testupload");
        req.setWorkspaceId(wsId);
        req.setOriginatorType("ORIGINATOR_TYPE_CUSTOMER");
        var res = documentService.uploadDocument(req, file, token);
        assertNull(res.getMessage());
        assertEquals(doc.getId(), ((DocumentDto)res.getData()).getId());

        ArgumentCaptor<WorkspaceDocumentRequest> wsDocCaptor = ArgumentCaptor.forClass(WorkspaceDocumentRequest.class);
        verify(webClient).addDocumentToWorkspace(wsDocCaptor.capture());
        assertEquals(wsId, wsDocCaptor.getValue().getId());
        assertTrue(wsDocCaptor.getValue().isCustomerDocument());
        ArgumentCaptor<UserCreateNotificationRequest> notificationCaptor = ArgumentCaptor.forClass(UserCreateNotificationRequest.class);
        verify(webClient).createNotification(notificationCaptor.capture());
        assertEquals("UPLOAD", notificationCaptor.getValue().getActionType());
        assertEquals(caseDto.getSuperOfficeId(), notificationCaptor.getValue().getCaseName());
        ArgumentCaptor<DocumentEmailRequest> emailCaptor = ArgumentCaptor.forClass(DocumentEmailRequest.class);
        verify(webClient).triggerUploadEmail(emailCaptor.capture());
        assertEquals(agent.getEmailAddress(), emailCaptor.getValue().getAgentEmail());
        assertEquals(caseDto.getCustomerEmail(), emailCaptor.getValue().getCustomerEmail());
        ArgumentCaptor<CaseManagementRequest> caseCaptor = ArgumentCaptor.forClass(CaseManagementRequest.class);
        verify(webClient).setDocumentActionForCase(any(), caseCaptor.capture());
        assertTrue(caseCaptor.getValue().getNeedAgentNotification());
        assertNotNull(caseCaptor.getValue().getLastUpdate());
    }

    @Test
    void getDocumentByCaseId_happyCase() {
        var caseId = UUID.randomUUID();
        var entity = new Document();
        entity.setId(UUID.randomUUID());
        entity.setCaseId(caseId);
        when(documentRepository.findAllByCaseId(caseId)).thenReturn(Arrays.asList(entity));

        var res = documentService.getDocumentByCaseId(caseId.toString());
        assertNotNull(res.getData());
        assertEquals(caseId, ((DocumentDto)res.getData().get(0)).getCaseId());
    }

    @Test
    void getDocumentByListIds_happyCase() {
        var id = UUID.randomUUID();
        var entity = new Document();
        entity.setId(id);
        when(documentRepository.findAllByIdIn(Arrays.asList(id))).thenReturn(Arrays.asList(entity));

        var res = documentService.getDocumentByListIds(id.toString());
        assertEquals(1, res.getData().size());
        assertEquals(id, ((DocumentDto)res.getData().get(0)).getId());
    }

    @Test
    void getDocumentById_happyCase() {
        var id = UUID.randomUUID();
        var entity = new Document();
        entity.setId(id);
        when(documentRepository.findById(id)).thenReturn(Optional.of(entity));

        var res = documentService.getDocumentById(id.toString());
        assertEquals(id, ((DocumentDto)res.getData()).getId());
    }

    @Test
    void updateDocument_happyCase() {
        var id = UUID.randomUUID();
        var customerToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                "eyJtZW1iZXJfaWQiOiIzODczMWE3MC01ZjMxLTRkOGEtYjhhMi0yMmQ1ZWFkODBhYzQiLCJzdWIiOiJzdGF0aWMtand0IiwiaXNzIjoic2VjdXJlLnVwbG9hZCI" +
                "sInByZWZlcnJlZF91c2VybmFtZSI6IkNVU1RPTUVSIiwiZXhwIjoxNjQyMzY3ODIwfQ." +
                "vmHGQA5Vkn039lHTs78u4VaqYn9tt56J3Z_ZX_H-jXA";
        var caseId = UUID.randomUUID();
        var caseDto = new CaseDto();
        caseDto.setId(caseId);
        caseDto.setSuperOfficeId("superOfficeId");
        caseDto.setStatus("OPEN");
        var agent = new UserDto();
        agent.setId(UUID.randomUUID());
        agent.setEmailAddress("agent@email.com");
        caseDto.setAssignedAgent(agent);
        var entity = new Document();
        entity.setId(id);
        entity.setCaseId(caseId);
        when(documentRepository.getById(id)).thenReturn(entity);
        when(webClient.getCaseById(caseId.toString())).thenReturn(caseDto);
        when(documentRepository.save(any())).thenReturn(entity);

        var req = new DocumentManagementRequest();
        req.setDescription("update label");
        var res = documentService.updateDocument(id.toString(), req, customerToken);
        assertEquals(id, ((DocumentDto)res.getData()).getId());

        ArgumentCaptor<UserCreateNotificationRequest> notificationCaptor = ArgumentCaptor.forClass(UserCreateNotificationRequest.class);
        verify(webClient).createNotification(notificationCaptor.capture());
        assertEquals("EDIT", notificationCaptor.getValue().getActionType());
        assertEquals(caseDto.getSuperOfficeId(), notificationCaptor.getValue().getCaseName());
    }

    @Test
    void deleteDocument_happyCase() {
        var id = UUID.randomUUID();
        var caseId = UUID.randomUUID();
        var entity = new Document();
        entity.setId(id);
        entity.setCaseId(caseId);
        entity.setFilename("filename");
        when(documentRepository.getById(id)).thenReturn(entity);

        var res = documentService.deleteDocument(id.toString());
        assertNull(res.getMessage());
        ArgumentCaptor<String> s3Captor = ArgumentCaptor.forClass(String.class);
        verify(amazonClient).removeObject(s3Captor.capture());
        assertEquals(entity.getId().toString(), s3Captor.getValue());
        ArgumentCaptor<CaseManagementRequest> caseCaptor = ArgumentCaptor.forClass(CaseManagementRequest.class);
        verify(webClient).setDocumentActionForCase(any(), caseCaptor.capture());
        assertNull(caseCaptor.getValue().getLastUpdate());
        assertFalse(caseCaptor.getValue().getNeedAgentNotification());
    }

    @Test
    void deleteDocumentsByCase_happyCase() {
        var caseId = UUID.randomUUID();
        var entity = new Document();
        entity.setId(UUID.randomUUID());
        entity.setCaseId(caseId);
        entity.setFilename("filename");
        when(documentRepository.findAllByCaseId(caseId)).thenReturn(Arrays.asList(entity));

        var res = documentService.deleteDocumentsByCase(caseId.toString());
        assertNull(res.getMessage());
        ArgumentCaptor<String> s3Captor = ArgumentCaptor.forClass(String.class);
        verify(amazonClient).removeObject(s3Captor.capture());
        assertEquals(entity.getId().toString(), s3Captor.getValue());
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        verify(documentRepository).deleteById(captor.capture());
        assertEquals(entity.getId(), captor.getValue());
    }

    @Test
    void getDocumentContent_happyCase() {
        var id = UUID.randomUUID();
        var entity = new Document();
        entity.setId(UUID.randomUUID());
        entity.setFilename("filename");
        when(documentRepository.findById(id)).thenReturn(Optional.of(entity));
        byte[] content = "content".getBytes(StandardCharsets.UTF_8);
        when(amazonClient.readObject(entity.getId().toString())).thenReturn(content);

        var res = documentService.getDocumentContent(id.toString());
        assertEquals(Base64.getEncoder().encodeToString(content), res.getData());
    }

    @Test
    void bulkUpdateDocument_happyCase() {
        var id = UUID.randomUUID();
        var entity = new Document();
        entity.setId(id);
        when(documentRepository.findAllByIdIn(Arrays.asList(id))).thenReturn(Arrays.asList(entity));

        var req = new DocumentManagementRequest();
        req.setDocIds(id.toString());
        req.setSignicatRequestId("signicatReqId");
        req.setSignicatTaskId("signicatTaskId");
        var res = documentService.bulkUpdateDocument(req);
        assertNull(res.getMessage());
        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(documentRepository).saveAll(captor.capture());
        assertEquals(req.getSignicatRequestId(), captor.getValue().get(0).getSignicatRequestId());
        assertEquals(req.getSignicatTaskId(), captor.getValue().get(0).getSignicatTaskId());
    }

}
