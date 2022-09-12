package com.tenerity.nordic.controller;

import com.tenerity.nordic.dto.*;
import com.tenerity.nordic.service.AuthenticationService;
import com.tenerity.nordic.service.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CommunicationService communicationService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<EntityResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authenticationService.login(request);
        if (loginResponse.getMessage() != null) {
            return ResponseEntity.badRequest().body(loginResponse);
        }
        return ResponseEntity.ok().body(loginResponse);
    }

    @GetMapping("/login/user-info")
    @ResponseBody
    public ResponseEntity<LoginUserInfoResponse> getLoginUserInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        LoginUserInfoResponse response = authenticationService.getLoginUserInfo(accessToken);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/login/customer")
    @ResponseBody
    public ResponseEntity<EntityResponse> customerLogin(@RequestBody CustomerLoginRequest request) {
        LoginResponse loginResponse = authenticationService.customerLogin(request);
        if (loginResponse.getMessage() != null) {
            return ResponseEntity.badRequest().body(loginResponse);
        }
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("/login/forget-password")
    @ResponseBody
    public ResponseEntity<EntityResponse> forgetPassword(@RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = communicationService.forgetPassword(request);
        if (response.getMessage() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/login/verify-token")
    @ResponseBody
    public ResponseEntity<EntityResponse> verifyToken(@RequestParam String token) {
        ResetPasswordResponse response = authenticationService.verifyToken(token);
        if (response.getMessage() != null) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/login/reset-password")
    @ResponseBody
    public ResponseEntity<EntityResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = authenticationService.resetPassword(request);
        if (response.getMessage() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

}
