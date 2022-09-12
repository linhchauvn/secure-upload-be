package com.tenerity.nordic.controller;

import com.tenerity.nordic.dto.EntityResponse;
import com.tenerity.nordic.dto.UserCreateNotificationRequest;
import com.tenerity.nordic.dto.UserNotificationStatisticResponse;
import com.tenerity.nordic.dto.UserSearchNotificationRequest;
import com.tenerity.nordic.dto.UserNotificationResponse;
import com.tenerity.nordic.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UserNotificationController {

    @Autowired
    private UserNotificationService userNotificationService;

    @PutMapping("/notification/{id}/read")
    @ResponseBody
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable String id) {
        UserNotificationResponse response = userNotificationService.markAsRead(id);
        if (response.getHttpCode() != null) {
            return ResponseEntity.status(response.getHttpCode()).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notification/{userId}/statistic")
    @ResponseBody
    public ResponseEntity<EntityResponse> getNotificationNumber(@PathVariable String userId) {
        UserNotificationStatisticResponse response = userNotificationService.getNotificationStatistic(userId);
        if (response.getMessage() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/notification/search")
    @ResponseBody
    public ResponseEntity<EntityResponse> searchNotification(@RequestBody UserSearchNotificationRequest request) {
        UserNotificationResponse response = userNotificationService.getNotification(request);
        if (response.getMessage() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/notification/create")
    @ResponseBody
    public ResponseEntity<EntityResponse> createNotification(@RequestBody UserCreateNotificationRequest request) {
        UserNotificationResponse response = userNotificationService.createNotification(request);
        if (response.getMessage() != null) {
            return ResponseEntity.status(response.getHttpCode()).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

}
