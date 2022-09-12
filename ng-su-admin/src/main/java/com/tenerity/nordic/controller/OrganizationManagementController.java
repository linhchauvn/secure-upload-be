package com.tenerity.nordic.controller;

import com.tenerity.nordic.dto.AdminDataResponse;
import com.tenerity.nordic.dto.AdminPanelManagementResponse;
import com.tenerity.nordic.dto.AdminPanelSearchRequest;
import com.tenerity.nordic.dto.AdminPanelSearchResponse;
import com.tenerity.nordic.dto.EntityResponse;
import com.tenerity.nordic.dto.OrganizationManagementRequest;
import com.tenerity.nordic.service.OrganizationManagementService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OrganizationManagementController {

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @GetMapping("/all-third-parties")
    @ResponseBody
    public ResponseEntity<EntityResponse> getAllThirdParties() {
        AdminDataResponse response = organizationManagementService.getAllThirdParties();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/thirdparty/search")
    @ResponseBody
    public ResponseEntity<EntityResponse> searchAgents(@RequestBody AdminPanelSearchRequest request) {
        AdminPanelSearchResponse searchResponse = organizationManagementService.searchOrganization(request);
        if (searchResponse.getMessage() != null) {
            return ResponseEntity.badRequest().body(searchResponse);
        }
        return ResponseEntity.ok().body(searchResponse);
    }

    @GetMapping("/thirdparty/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse> getAgent(@PathVariable String id) {
        AdminPanelManagementResponse response = organizationManagementService.findOrganizationById(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/thirdparty/create")
    @ResponseBody
    public ResponseEntity<EntityResponse> createAgent(@RequestBody OrganizationManagementRequest request) {
        AdminPanelManagementResponse response = organizationManagementService.createOrganization(request);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/thirdparty/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse> updateAgent(@PathVariable String id, @RequestBody OrganizationManagementRequest request) {
        AdminPanelManagementResponse response = organizationManagementService.updateOrganization(id, request);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/thirdparty/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAgent(@PathVariable String id) {
        AdminPanelManagementResponse response = organizationManagementService.deleteOrganizationById(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }
}
