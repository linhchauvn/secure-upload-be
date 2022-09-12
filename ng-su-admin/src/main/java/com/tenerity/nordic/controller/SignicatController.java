package com.tenerity.nordic.controller;

import com.tenerity.nordic.client.dto.SignicatSignOrderStatusResponse;
import com.tenerity.nordic.service.SignicatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin
public class SignicatController {

    @Autowired
    private SignicatService signicatService;

    @GetMapping("/signicat/all-methods")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getSignicatAuthorizationMethods(@RequestParam String workspaceId,
                                                                               @RequestParam (required = false) String locale) {
        var urlMap =  signicatService.getSignicatAuthorizationUrl(workspaceId, locale);
        return ResponseEntity.ok().body(urlMap);
    }

    @GetMapping("/signicat/signing-url")
    @ResponseBody
    public ResponseEntity<String> getSignicatSigningUrl(
                                                        @RequestParam String host,
                                                        @RequestParam String docIds,
                                                        @RequestParam String locale) {
        String response = signicatService.getSignicatSigningUrl(docIds, locale, host);
        if (StringUtils.isBlank(response)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/signicat/signing-status")
    @ResponseBody
    public ResponseEntity<SignicatSignOrderStatusResponse> getOrderStatus(@RequestParam String requestId,
                                                                          @RequestParam String taskId) {
        var response =  signicatService.getOrderStatus(requestId, taskId);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().body(response);
    }

//    @GetMapping("/signicat/packaging-result")
//    @ResponseBody
//    public ResponseEntity<DocumentEsignContentResponse> getPackagingTaskResult(@RequestParam String requestId,
//                                                                               @RequestParam String packagingTaskId) {
//        var response =  signicatService.getPackagingTaskResult(requestId, packagingTaskId);
//        if (response == null || response.getContent() == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//        return ResponseEntity.ok().body(response);
//    }
}