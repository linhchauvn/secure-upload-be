package com.tenerity.nordic.controller;

import com.tenerity.nordic.dto.AdminDataResponse;
import com.tenerity.nordic.dto.AdminPanelManagementResponse;
import com.tenerity.nordic.dto.AdminPanelSearchRequest;
import com.tenerity.nordic.dto.AdminPanelSearchResponse;
import com.tenerity.nordic.dto.ClientManagementRequest;
import com.tenerity.nordic.dto.EntityResponse;
import com.tenerity.nordic.service.ClientManagementService;
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
public class ClientManagementController {

    @Autowired
    private ClientManagementService clientManagementService;

    @GetMapping("/all-clients")
    @ResponseBody
    public ResponseEntity<EntityResponse> getAllClients() {
        AdminDataResponse response = clientManagementService.getAllClients();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/client/search")
    @ResponseBody
    public ResponseEntity<EntityResponse> searchAgents(@RequestBody AdminPanelSearchRequest request) {
        AdminPanelSearchResponse searchResponse = clientManagementService.searchClient(request);
        if (searchResponse.getMessage() != null) {
            return ResponseEntity.badRequest().body(searchResponse);
        }
        return ResponseEntity.ok().body(searchResponse);
    }

    @GetMapping("/client/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse> getAgent(@PathVariable String id) {
        AdminPanelManagementResponse response = clientManagementService.findClientById(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/client/create")
    @ResponseBody
    public ResponseEntity<EntityResponse> createAgent(@RequestBody ClientManagementRequest request) {
        AdminPanelManagementResponse response = clientManagementService.createClient(request);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/client/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse> updateAgent(@PathVariable String id, @RequestBody ClientManagementRequest request) {
        AdminPanelManagementResponse response = clientManagementService.updateClient(id, request);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/client/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAgent(@PathVariable String id) {
        AdminPanelManagementResponse response = clientManagementService.deleteClientById(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }
}
