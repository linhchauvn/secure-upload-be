package com.tenerity.nordic.controller;

import com.tenerity.nordic.dto.DocumentDownloadResponse;
import com.tenerity.nordic.dto.DocumentListResponse;
import com.tenerity.nordic.dto.DocumentManagementRequest;
import com.tenerity.nordic.dto.DocumentManagementResponse;
import com.tenerity.nordic.dto.DocumentUploadRequest;
import com.tenerity.nordic.dto.DocumentUploadResponse;
import com.tenerity.nordic.dto.EntityResponse;
import com.tenerity.nordic.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload-file")
    @ResponseBody
    public ResponseEntity<DocumentUploadResponse> uploadFile(@RequestPart(value = "data") DocumentUploadRequest request,
                                                             @RequestPart(value = "file") MultipartFile file,
                                                             @RequestHeader(value = "Authorization", required = false) String authToken) {
        DocumentUploadResponse response = documentService.uploadDocument(request, file, authToken);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/case-document/{caseId}")
    @ResponseBody
    public ResponseEntity<DocumentListResponse> getDocumentsByCase(@PathVariable String caseId) {
        DocumentListResponse response = documentService.getDocumentByCaseId(caseId);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/documents")
    @ResponseBody
    public ResponseEntity<DocumentListResponse> getDocumentList(@RequestParam String docIds) {
        DocumentListResponse response = documentService.getDocumentByListIds(docIds);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/document/{id}")
    @ResponseBody
    public ResponseEntity<DocumentManagementResponse> getDocument(@PathVariable String id) {
        DocumentManagementResponse response = documentService.getDocumentById(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/document/{id}/content")
    @ResponseBody
    public ResponseEntity<DocumentManagementResponse> getDocumentContent(@PathVariable String id) {
        DocumentManagementResponse response = documentService.getDocumentContent(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/document/{id}")
    @ResponseBody
    public ResponseEntity<DocumentManagementResponse> updateDocument(@PathVariable String id,
                                                                     @RequestBody DocumentManagementRequest request,
                                                                     @RequestHeader(value = "Authorization", required = false) String authToken) {
        DocumentManagementResponse response = documentService.updateDocument(id, request, authToken);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/document/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        DocumentManagementResponse response = documentService.deleteDocument(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/document/case/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteDocumentByCase(@PathVariable String id) {
        DocumentManagementResponse response = documentService.deleteDocumentsByCase(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/document/bulk-update")
    @ResponseBody
    public ResponseEntity<DocumentManagementResponse> bulkUpdateDocument(@RequestBody DocumentManagementRequest request) {
        DocumentManagementResponse response = documentService.bulkUpdateDocument(request);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/document/{id}/download")
    @ResponseBody
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String id,
                                                   @RequestHeader(value = "clientIp", required = false) String clientIp) {
        DocumentDownloadResponse response = documentService.downloadFile(id, clientIp);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }

        return ResponseEntity.ok()
                .contentType(response.getContentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + response.getFilename() + "\"")
                .body(response.getContentData());
    }

    @PostMapping("/document/{id}/upload-signicat-file")
    @ResponseBody
    public ResponseEntity<EntityResponse> uploadSignicatFile(@PathVariable String id,
                                                             @RequestPart(value = "file") MultipartFile file) {
        var response = documentService.uploadSignicatFile(id, file);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }
}