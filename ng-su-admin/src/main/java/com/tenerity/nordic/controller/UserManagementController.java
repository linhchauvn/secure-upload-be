package com.tenerity.nordic.controller;

import com.tenerity.nordic.dto.AdminDataResponse;
import com.tenerity.nordic.dto.AdminPanelManagementResponse;
import com.tenerity.nordic.dto.AdminPanelSearchRequest;
import com.tenerity.nordic.dto.AdminPanelSearchResponse;
import com.tenerity.nordic.dto.EntityResponse;
import com.tenerity.nordic.dto.UserManagementRequest;
import com.tenerity.nordic.enums.UserRole;
import com.tenerity.nordic.service.UserManagementService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@CrossOrigin
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @GetMapping("/all-agents")
    @ResponseBody
    public ResponseEntity<EntityResponse> getAllAgents() {
        AdminDataResponse response = userManagementService.getAllAgents();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/agent/search")
    @ResponseBody
    public ResponseEntity<EntityResponse> searchAgents(@RequestBody AdminPanelSearchRequest request) {
        AdminPanelSearchResponse searchResponse = userManagementService.searchUser(request, Arrays.asList(UserRole.ADMIN, UserRole.AGENT));
        if (searchResponse.getMessage() != null) {
            return ResponseEntity.badRequest().body(searchResponse);
        }
        return ResponseEntity.ok().body(searchResponse);
    }

    @GetMapping("/agent/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse> getAgent(@PathVariable String id) {
        AdminPanelManagementResponse response = userManagementService.findAgentById(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/agent/create")
    @ResponseBody
    public ResponseEntity<EntityResponse> createAgent(@RequestBody UserManagementRequest request) {
        AdminPanelManagementResponse response = userManagementService.createUser(request, false);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/agent/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse> updateAgent(@PathVariable String id,
                                                      @RequestBody UserManagementRequest request) {
        AdminPanelManagementResponse response = userManagementService.updateUser(id, request, false);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/agent/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteAgent(@PathVariable String id) {
        AdminPanelManagementResponse response = userManagementService.deleteUserById(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/thirdparty-user/search")
    @ResponseBody
    public ResponseEntity<EntityResponse> searchThirdPartyUser(@RequestBody AdminPanelSearchRequest request) {
        AdminPanelSearchResponse searchResponse = userManagementService.searchUser(request, Arrays.asList(UserRole.THIRDPARTY));
        if (searchResponse.getMessage() != null) {
            return ResponseEntity.badRequest().body(searchResponse);
        }
        return ResponseEntity.ok().body(searchResponse);
    }

    @GetMapping("/thirdparty-user/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse> getThirdPartyUser(@PathVariable String id) {
        AdminPanelManagementResponse response = userManagementService.findThirdPartyUserById(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/thirdparty-user/create")
    @ResponseBody
    public ResponseEntity<EntityResponse> createThirdPartyUser(@RequestBody UserManagementRequest request) {
        AdminPanelManagementResponse response = userManagementService.createUser(request, true);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/thirdparty-user/{id}")
    @ResponseBody
    public ResponseEntity<EntityResponse> updateThirdPartyUser(@PathVariable String id,
                                                               @RequestBody UserManagementRequest request) {
        AdminPanelManagementResponse response = userManagementService.updateUser(id, request, true);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/thirdparty-user/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteThirdPartyUser(@PathVariable String id) {
        AdminPanelManagementResponse response = userManagementService.deleteUserById(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }

}
