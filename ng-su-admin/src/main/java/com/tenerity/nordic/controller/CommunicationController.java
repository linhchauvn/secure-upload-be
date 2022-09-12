package com.tenerity.nordic.controller;

import com.tenerity.nordic.dto.DocumentEmailRequest;
import com.tenerity.nordic.dto.EntityResponse;
import com.tenerity.nordic.dto.WorkspaceDeletionRequest;
import com.tenerity.nordic.service.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class CommunicationController {

    @Autowired
    private CommunicationService communicationService;

    @PostMapping("/send-case-update-email")
    @ResponseBody
    public ResponseEntity<EntityResponse> caseUpdate(@RequestBody DocumentEmailRequest request) {
        EntityResponse response = communicationService.caseUpdateEmail(request);
        if (response.getMessage() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/send-workspace-deletion-email")
    @ResponseBody
    public ResponseEntity<EntityResponse> deleteWorkspaceEmail(@RequestBody WorkspaceDeletionRequest request) {
        EntityResponse response = communicationService.triggerWorkspaceDeletionEmail(request);
        if (response.getMessage() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }
}
