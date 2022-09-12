package com.tenerity.nordic.controller;

import com.tenerity.nordic.dto.CaseManagementResponse;
import com.tenerity.nordic.dto.WorkspaceDataListResponse;
import com.tenerity.nordic.dto.WorkspaceDocumentRequest;
import com.tenerity.nordic.dto.WorkspaceManagementRequest;
import com.tenerity.nordic.dto.EntityResponse;
import com.tenerity.nordic.service.WorkspaceService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;

@RestController
@CrossOrigin
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("/workspace/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse> getWorkspace(@PathVariable String id,
                                                       @RequestHeader(value = "Authorization", required = false) String authToken,
                                                       @RequestHeader(value = "clientIp", required = false) String clientIp) {
        CaseManagementResponse response = workspaceService.getWorkspaceById(id, authToken, clientIp);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/workspace/create")
    @ResponseBody
    public ResponseEntity<EntityResponse> createWorkspace(@RequestBody WorkspaceManagementRequest request) {
        CaseManagementResponse response = workspaceService.createWorkspace(request);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/workspace/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteWorkspace(@PathVariable String id) {
        CaseManagementResponse response = workspaceService.deleteWorkspace(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/workspace/{id}/request-delete")
    @ResponseBody
    public ResponseEntity<Void> requestDeleteWorkspace(@PathVariable String id) {
        CaseManagementResponse response = workspaceService.requestDeleteWorkspace(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/workspace/third-party/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteWorkspaceByThirdPartyId(@PathVariable String id) {
        CaseManagementResponse response = workspaceService.deleteWorkspaceByThirdPartyId(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/workspace/add-document")
    @ResponseBody
    public ResponseEntity<CaseManagementResponse> addDocumentToWorkspace(@RequestBody WorkspaceDocumentRequest request,
                                                                         @RequestHeader(value = "Authorization", required = false) String authToken) {
        CaseManagementResponse response = workspaceService.addDocuments(request, authToken);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/workspace/remove-document")
    @ResponseBody
    public ResponseEntity<CaseManagementResponse> removeDocumentFromWorkspace(@RequestBody WorkspaceDocumentRequest request) {
        CaseManagementResponse response = workspaceService.removeDocuments(request);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/workspace/{id}/generate-token")
    @ResponseBody
    public ResponseEntity<CaseManagementResponse> generateToken(@PathVariable String id) {
        CaseManagementResponse response = workspaceService.generateToken(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/third-party-user/workspaces")
    @ResponseBody
    public ResponseEntity<WorkspaceDataListResponse> getWorkspaces(@RequestHeader(HttpHeaders.AUTHORIZATION) String userToken) {
        WorkspaceDataListResponse response = workspaceService.getThirdPartyUserWorkspaces(userToken);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/workspace/{id}/customer-token")
    @ResponseBody
    public ResponseEntity<String> validateCustomerToken(@PathVariable String id, @RequestParam String token) {
        var wsType = workspaceService.isValidCustomerToken(id, token);
        return ResponseEntity.ok().body(wsType);
    }

    @GetMapping("/workspace/{id}/customer-signicat")
    @ResponseBody
    public ResponseEntity<String> authorizeSignicatUser(@PathVariable String id, @RequestParam String nationalId) {
        Boolean isValid = workspaceService.isAuthorizedSignicatUser(id, nationalId);
        return ResponseEntity.ok().body(isValid.toString());
    }

    @GetMapping("/workspace/{id}/client-locale")
    @ResponseBody
    public ResponseEntity<String> authorizeSignicatUser(@PathVariable String id) {
        String response = workspaceService.getClientLocale(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/workspace-document/{docId}")
    @ResponseBody
    public ResponseEntity<CaseManagementResponse> deleteDocumentReference(@PathVariable String docId) {
        CaseManagementResponse response = workspaceService.deleteDocumentReference(docId);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().build();
    }
}
