package com.tenerity.nordic.controller;

import com.tenerity.nordic.dto.CaseManagementRequest;
import com.tenerity.nordic.dto.CaseManagementResponse;
import com.tenerity.nordic.dto.CaseSearchRequest;
import com.tenerity.nordic.dto.CaseSearchResponse;
import com.tenerity.nordic.dto.CasesStatisticResponse;
import com.tenerity.nordic.dto.EntityResponse;
import com.tenerity.nordic.service.SchedulerService;
import com.tenerity.nordic.service.CaseService;
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
@PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
public class CaseController {

    @Autowired
    private CaseService caseService;

    @Autowired
    private SchedulerService schedulerService;

    @GetMapping("/cases-statistics")
    @ResponseBody
    public ResponseEntity<CasesStatisticResponse> getCasesStatistics() {
        CasesStatisticResponse response = caseService.getCasesStatistics();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/search")
    @ResponseBody
    public ResponseEntity<CaseSearchResponse> search(@RequestBody CaseSearchRequest request) {
        CaseSearchResponse response = caseService.searchCase(request);
        if (response.getHttpCode() != null && response.getHttpCode() == HttpStatus.BAD_REQUEST.value()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/case/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse> getCase(@PathVariable String id) {
        CaseManagementResponse response = caseService.getCaseById(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/case/create")
    @ResponseBody
    public ResponseEntity<EntityResponse> createCase(@RequestBody CaseManagementRequest request) {
        CaseManagementResponse response = caseService.createCase(request);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/case/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse> updateCase(@PathVariable String id, @RequestBody CaseManagementRequest request) {
        CaseManagementResponse response = caseService.updateCase(id, request);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/case/{id}/doc-action")
    @ResponseBody
    public ResponseEntity<EntityResponse> setDocumentActionForCase(@PathVariable String id, @RequestBody CaseManagementRequest request) {
        CaseManagementResponse response = caseService.setDocumentActionForCase(id, request);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/case/{id}")
    @ResponseBody
    public ResponseEntity<Void> resolveCase(@PathVariable String id) {
        CaseManagementResponse response = caseService.closeCase(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/case/client/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteCaseByCLient(@PathVariable String id) {
        CaseManagementResponse response = caseService.deleteCaseByClient(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }
}
