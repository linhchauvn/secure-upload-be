package com.tenerity.nordic.client;

import com.tenerity.nordic.client.dto.*;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class InternalWebClient {
    Logger logger = LoggerFactory.getLogger(InternalWebClient.class);

    @Value("${basicAuth.username}")
    private String username;
    @Value("${basicAuth.password}")
    private String password;

    @Value("${internal.caseServiceBaseUrl}")
    private String caseServiceBaseUrl;
    @Value("${internal.adminServiceBaseUrl}")
    private String adminServiceBaseUrl;
    @Value("${internal.addDocumentToWorkspacePath}")
    private String addDocumentToWorkspacePath;
    @Value("${internal.deleteDocumentReferencePath}")
    private String deleteDocumentReferencePath;
    @Value("${internal.setDocumentActionForCasePath}")
    private String setDocumentActionForCasePath;
    @Value("${internal.getCasePath}")
    private String getCasePath;
    @Value("${internal.getWorkspacePath}")
    private String getWorkspacePath;
    @Value("${internal.createNotificationPath}")
    private String createNotificationPath;
    @Value("${internal.getAgentByIdApiPath}")
    private String getAgentByIdApiPath;
    @Value("${internal.getThirdPartyUserByIdApiPath}")
    private String getThirdPartyUserByIdApiPath;
    @Value("${internal.triggerUploadDocumentEmail}")
    private String triggerUploadDocumentEmail;
    @Value("${internal.createAuditData}")
    private String createAuditData;
//    @Value("${internal.getPackagingTaskResultPath}")
//    private String getPackagingTaskResultPath;

    WebClient caseService = null;
    WebClient adminService = null;

    final HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
            .responseTimeout(Duration.ofMillis(30000))
            .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(30000, TimeUnit.MILLISECONDS))
                                        .addHandlerLast(new WriteTimeoutHandler(30000, TimeUnit.MILLISECONDS)));

    private WebClient getCaseService() {
        if (caseService == null) {
            caseService = WebClient.builder()
                    .baseUrl(caseServiceBaseUrl)
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .defaultHeaders(header -> header.setBasicAuth(username, password))
                    .build();
        }
        return caseService;
    }

    private WebClient getAdminService() {
        if (adminService == null) {
            adminService = WebClient.builder()
                    .baseUrl(adminServiceBaseUrl)
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .defaultHeaders(header -> header.setBasicAuth(username, password))
                    .build();
        }
        return adminService;
    }

    public void addDocumentToWorkspace(WorkspaceDocumentRequest request) {
        try {
            var response = getCaseService().put().uri(uriBuilder -> uriBuilder.path(addDocumentToWorkspacePath).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(request), WorkspaceDocumentRequest.class)
                    .retrieve().bodyToMono(new ParameterizedTypeReference<CaseManagementResponse<WorkspaceDto>>(){});
            var workspaceDto = response.block().getData();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public void setDocumentActionForCase(UUID caseId, CaseManagementRequest request) {
        try {
            var realPath = String.format(setDocumentActionForCasePath, caseId);
            var response = getCaseService().put().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(request), CaseManagementRequest.class)
                    .retrieve().bodyToMono(new ParameterizedTypeReference<CaseManagementResponse<CaseManagementResponse>>(){});
            var workspaceDto = response.block().getData();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public CaseDto getCaseById(String caseId) {
        try {
            var realPath = String.format(getCasePath, caseId);
            Mono<CaseManagementResponse<CaseDto>> response = getCaseService().get().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<CaseManagementResponse<CaseDto>>(){});
            return response.block().getData();
        } catch (Exception e) {
            return null;
        }
    }

    public WorkspaceDto getWorkspaceById(String workspaceId) {
        try {
            var realPath = String.format(getWorkspacePath, workspaceId);
            Mono<CaseManagementResponse<WorkspaceDto>> response = getCaseService().get().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<CaseManagementResponse<WorkspaceDto>>(){});
            return response.block().getData();
        } catch (Exception e) {
            return null;
        }
    }

    public void createNotification(UserCreateNotificationRequest request) {
        try {
            var response = getAdminService().post().uri(uriBuilder -> uriBuilder.path(createNotificationPath).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve().toBodilessEntity();
            response.block();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public UserDto getAgentById(UUID id) {
        try {
            var realPath = String.format(getAgentByIdApiPath, id.toString());
            Mono<AdminPanelManagementResponse<UserDto>> response = getAdminService().get().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<AdminPanelManagementResponse<UserDto>>(){});
            return (UserDto) response.block().getData();
        } catch (Exception e) {
            return null;
        }
    }

    public UserDto getThirdPartyUserById(String id) {
        try {
            var realPath = String.format(getThirdPartyUserByIdApiPath, id);
            Mono<AdminPanelManagementResponse<UserDto>> response = getAdminService().get().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<AdminPanelManagementResponse<UserDto>>(){});
            return (UserDto) response.block().getData();
        } catch (Exception e) {
            return null;
        }
    }

    public void triggerUploadEmail(DocumentEmailRequest request){
        try {
            var response = getAdminService().post().uri(uriBuilder -> uriBuilder.path(triggerUploadDocumentEmail).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve().bodyToMono(String.class);
            var data = response.block();
            logger.info(data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public boolean createAuditData(AuditCreationRequest request){
        Object data = null;
        try {
            var response = getAdminService().post().uri(uriBuilder -> uriBuilder.path(createAuditData).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve().bodyToMono(Object.class);
            data = response.block();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return data != null;
    }

    public void deleteDocumentReferencePath(String docId) {
        try {
            var realPath = String.format(deleteDocumentReferencePath, docId);
            var response = getCaseService().delete().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().bodyToMono(String.class);
            response.block();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

//    public DocumentEsignContentResponse getPackagingTaskResult(String requestId, String packagingTaskId){
//        try {
//            var response = getAdminService().get()
//                    .uri(uriBuilder -> uriBuilder.path(getPackagingTaskResultPath)
//                            .queryParam("requestId", "270120222hwr22fzpgtvkhgqsw0tuok77ktjl6mxxae9xakvwtvvbmnod7")
//                            .queryParam("packagingTaskId", "9cebd24e-cd04-458e-a760-2b89977be740").build())
//                    .retrieve().bodyToMono(new ParameterizedTypeReference<DocumentEsignContentResponse>(){});
//            return response.block();
//        } catch (Exception e) {
//            logger.error("[getPackagingTaskResult ERROR] ", e.getMessage());
//            return null;
//        }
//    }

}
