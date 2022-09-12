package com.tenerity.nordic.service;

import com.tenerity.nordic.client.NgAuthWebClient;
import com.tenerity.nordic.client.dto.CommunicationRequest;
import com.tenerity.nordic.client.dto.MessageConfiguration;
import com.tenerity.nordic.dto.*;
import com.tenerity.nordic.repository.UserRepository;
import com.tenerity.nordic.util.Constants;
import com.tenerity.nordic.util.CypherUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CommunicationService {

    @Value("${customJwt.key}")
    private String key;
    @Value("${customJwt.ivVector}")
    private String ivVector;
    @Value("${internal.resetPasswordUrl}")
    private String resetPasswordUrl;
    @Value("${internal.caseDetailUrl}")
    private String caseDetailUrl;

    @Value("${communication.senderIdentity}")
    private String senderIdentity;
    @Value("${communication.tenantId}")
    private String tenantId;
    @Value("${communication.applicationId}")
    private String applicationId;
    @Value("${communication.templates.resetPassword}")
    private String resetPasswordTemplateName;
    @Value("${communication.templates.caseUpdate}")
    private String caseUpdateTemplateName;
    @Value("${communication.templates.workspaceDeletion}")
    private String workspaceDeletion;
    @Value("${communication.templates.documentExceededOneFour}")
    private String documentExceededOneFour;
    @Value("${communication.templates.documentExceededSevenTwo}")
    private String documentExceededSevenTwo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Autowired
    private NgAuthWebClient webClient;

    public ResetPasswordResponse forgetPassword(ResetPasswordRequest request) {
        ResetPasswordResponse response = new ResetPasswordResponse();
        var user = userRepository.findUserByEmailAddress(request.getEmailAddress());
        if (!user.isPresent()) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage("Cannot find user by given email: " + request.getEmailAddress());
            return response;
        }
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("username", user.get().getUsername());
        obj.put("reset_password_link", this.createResetPasswordUrl(request.getHost(), user.get().getId()));
        MessageConfiguration messageConfiguration = new MessageConfiguration(this.senderIdentity, request.getEmailAddress(), resetPasswordTemplateName, obj);
        CommunicationRequest communicationRequest = new CommunicationRequest(this.applicationId, this.tenantId, messageConfiguration);
        webClient.sendingEmail(communicationRequest);
        return response;
    }

    public EntityResponse caseUpdateEmail(DocumentEmailRequest request){
        var response = new EntityResponse();
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("email", request.getCustomerEmail());
        obj.put("url", String.format(caseDetailUrl, request.getCaseId()));
        obj.put("documents", request.getFileData());
        obj.put("id", request.getId());
        String templateName = this.convertToPinpointTemplateName(request.getTemplateName());
        MessageConfiguration messageConfiguration = new MessageConfiguration(this.senderIdentity, request.getAgentEmail(), templateName, obj);
        CommunicationRequest communicationRequest = new CommunicationRequest(this.applicationId, this.tenantId, messageConfiguration);
        webClient.sendingEmail(communicationRequest);
        return response;
    }

    public EntityResponse triggerWorkspaceDeletionEmail(WorkspaceDeletionRequest request){
        var response = new EntityResponse();
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("workspaceLabel", request.getWorkspaceLabel());
        obj.put("workspaceUrl", request.getWorkspaceUrl());
        obj.put("customerEmail", request.getCustomerEmail());
        obj.put("caseUrl", request.getCaseUrl());
        obj.put("organizationName", request.getOrganizationName());

        MessageConfiguration messageConfiguration = new MessageConfiguration(this.senderIdentity, request.getAgentEmail(), workspaceDeletion, obj);
        CommunicationRequest communicationRequest = new CommunicationRequest(this.applicationId, this.tenantId, messageConfiguration);
        webClient.sendingEmail(communicationRequest);
        return response;
    }

    private String loadResetEmailTemplate(String host, UUID id){
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("url", createResetPasswordUrl(host, id));

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process("ResetPassword.html", thymeleafContext);
        return htmlBody;
    }

    private String createResetPasswordUrl(String host, UUID id) {
        String token = CypherUtils.rsaEncryptWithIvKey(id.toString(), key, ivVector);
        try {
            token = URLEncoder.encode(token, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {

        }
        String fullPath = String.format(resetPasswordUrl, host, token);
        return fullPath;
    }

    private String convertToPinpointTemplateName(String templateKey){
        switch (templateKey){
            case "Secure_Upload_Exceeded_24_hours":
                return documentExceededOneFour;
            case "Secure_Upload_Exceeded_72_hours":
                return documentExceededSevenTwo;
        }
        return caseUpdateTemplateName;
    }
}
