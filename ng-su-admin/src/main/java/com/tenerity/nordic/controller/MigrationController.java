package com.tenerity.nordic.controller;

import com.tenerity.nordic.dto.CustomerLoginRequest;
import com.tenerity.nordic.dto.EntityResponse;
import com.tenerity.nordic.dto.LoginRequest;
import com.tenerity.nordic.dto.LoginResponse;
import com.tenerity.nordic.dto.LoginUserInfoResponse;
import com.tenerity.nordic.dto.ResetPasswordRequest;
import com.tenerity.nordic.dto.ResetPasswordResponse;
import com.tenerity.nordic.service.AuthenticationService;
import com.tenerity.nordic.service.CommunicationService;
import com.tenerity.nordic.service.MigrationDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class MigrationController {

    @Autowired
    private MigrationDataService migrationDataService;

    @GetMapping("/syncUserData")
    @ResponseBody
    public ResponseEntity<EntityResponse> syncUserDataToKeycloak() {
        migrationDataService.syncUserDataToKeycloak();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/deleteKeyCloakUser")
    @ResponseBody
    public ResponseEntity<EntityResponse> deleteKeyCloakUser() {
        migrationDataService.deleteKeyCloakUser();
        return ResponseEntity.ok().build();
    }

}
