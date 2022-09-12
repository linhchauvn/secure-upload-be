package com.tenerity.nordic.controller;

import com.tenerity.nordic.dto.AuditCreationRequest;
import com.tenerity.nordic.dto.AuditCreationResponse;
import com.tenerity.nordic.dto.AuditDto;
import com.tenerity.nordic.dto.CaseManagementRequest;
import com.tenerity.nordic.dto.CaseManagementResponse;
import com.tenerity.nordic.dto.CaseSearchRequest;
import com.tenerity.nordic.dto.CaseSearchResponse;
import com.tenerity.nordic.dto.CasesStatisticResponse;
import com.tenerity.nordic.dto.EntityResponse;
import com.tenerity.nordic.service.AuditService;
import com.tenerity.nordic.service.CaseService;
import com.tenerity.nordic.service.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
public class AuditController {

    @Autowired
    private AuditService auditService;

    @PostMapping("/audit/create")
    @ResponseBody
    public ResponseEntity<AuditCreationResponse> createAuditData(AuditCreationRequest req) {
        AuditCreationResponse response = auditService.createAuditData(req);
        if (response.getHttpCode() != null) {
            ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }
}
