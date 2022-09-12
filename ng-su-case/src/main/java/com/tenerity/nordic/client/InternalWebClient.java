package com.tenerity.nordic.client;

import com.tenerity.nordic.client.dto.DocumentEmailRequest;
import com.tenerity.nordic.client.dto.UserCreateNotificationRequest;
import com.tenerity.nordic.client.dto.WorkspaceDeletionRequest;
import com.tenerity.nordic.util.ConfigProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class InternalWebClient {
    Logger logger = LoggerFactory.getLogger(InternalWebClient.class);

    @Value("${basicAuth.username}")
    private String username;
    @Value("${basicAuth.password}")
    private String password;

    WebClient adminService = null;
    WebClient documentService = null;
    @Autowired
    private ConfigProperties configProperties;
    private HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
            .responseTimeout(Duration.ofMillis(30000))
            .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(30000, TimeUnit.MILLISECONDS))
                                        .addHandlerLast(new WriteTimeoutHandler(30000, TimeUnit.MILLISECONDS)));

    public WebClient getAdminService() {
        if (adminService == null) {
            adminService = WebClient.builder()
                    .baseUrl(configProperties.getAdminServiceBaseUrl())
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .defaultHeaders(header -> header.setBasicAuth(username, password))
                    .build();
        }
        return adminService;
    }

    public WebClient getDocumentService() {
        if (documentService == null) {
            documentService = WebClient.builder()
                    .baseUrl(configProperties.getDocumentServiceBaseUrl())
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .defaultHeaders(header -> header.setBasicAuth(username, password))
                    .build();
        }
        return documentService;
    }

    public List<UserDto> getAllAgents() {
        var response = getAdminService().get().uri(uriBuilder -> uriBuilder.path(configProperties.getAllAgentsApiPath()).build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<AdminDataResponse<UserDto>>(){});
        return response.block().getData();
    }

    public List<ClientDto> getAllClients() {
        var response = getAdminService().get().uri(uriBuilder -> uriBuilder.path(configProperties.getAllClientsApiPath()).build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<AdminDataResponse<ClientDto>>(){});
        return response.block().getData();
    }

    public List<OrganizationDto> getAllThirdParties() {
        var response = getAdminService().get().uri(uriBuilder -> uriBuilder.path(configProperties.getAllThirdPartiesApiPath()).build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<AdminDataResponse<OrganizationDto>>(){});
        return response.block().getData();
    }

    public UserDto getAgentById(UUID id) {
        try {
            var realPath = String.format(configProperties.getGetAgentByIdApiPath(), id.toString());
            Mono<AdminPanelManagementResponse<UserDto>> response = getAdminService().get().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<AdminPanelManagementResponse<UserDto>>(){});
            return (UserDto) response.block().getData();
        } catch (Exception e) {
            return null;
        }
    }

    public UserDto getThirdPartyUserById(String uuid) {
        try {
            var realPath = String.format(configProperties.getGetThirdPartyUserByIdApiPath(), uuid);
            Mono<AdminPanelManagementResponse<UserDto>> response = getAdminService().get().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<AdminPanelManagementResponse<UserDto>>(){});
            return response.block().getData();
        } catch (Exception e) {
            return null;
        }
    }

    public ClientDto getClientById(UUID id) {
        try {
            var realPath = String.format(configProperties.getGetClientByIdApiPath(), id.toString());
            Mono<AdminPanelManagementResponse<ClientDto>> response = getAdminService().get().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<AdminPanelManagementResponse<ClientDto>>(){});
            return response.block().getData();
        } catch (Exception e) {
            return null;
        }
    }

    public OrganizationDto getOrganizationById(UUID id) {
        try {
            var realPath = String.format(configProperties.getGetOrganizationByIdApiPath(), id.toString());
            Mono<AdminPanelManagementResponse<OrganizationDto>> response = getAdminService().get().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<AdminPanelManagementResponse<OrganizationDto>>(){});
            return response.block().getData();
        } catch (Exception e) {
            return null;
        }
    }

    public List<DocumentDto> getDocumentList(String docIds) {
        var realPath = String.format(configProperties.getGetDocumentListPath(), docIds);
        var response = getDocumentService().get().uri(uriBuilder -> uriBuilder.path(realPath).queryParam("docIds", docIds).build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<DocumentListResponse<DocumentDto>>(){});
        return response.block().getData();
    }

    public List<DocumentDto> getDocumentByCaseId(String caseId) {
        var realPath = String.format(configProperties.getGetDocumentByCasePath(), caseId);
        var response = getDocumentService().get().uri(uriBuilder -> uriBuilder.path(realPath).build())
                .retrieve().bodyToMono(new ParameterizedTypeReference<DocumentListResponse<DocumentDto>>(){});
        return response.block().getData();
    }

    public void deleteDocumentByCaseId(String caseId) {
        try {
            var realPath = String.format(configProperties.getDeleteDocumentByCasePath(), caseId);
            var response = getDocumentService().delete().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().toBodilessEntity();
            response.block();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    public void createNotification(UserCreateNotificationRequest request) {
        try {
            var response = getAdminService().post().uri(uriBuilder -> uriBuilder.path(configProperties.getCreateNotificationPath()).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve().toBodilessEntity();
            response.block();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void triggerUploadEmail(DocumentEmailRequest request){
        try {
            var response = getAdminService().post().uri(uriBuilder -> uriBuilder.path(configProperties.getTriggerUploadDocumentEmail()).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve().toBodilessEntity();
            response.doOnSuccess(success -> logger.info("Successfully send email to: " + request.getAgentEmail() + ". success: " + success))
                    .doOnError(error -> logger.info("Unsuccessfully send email to: " + request.getAgentEmail() + ". error: " + error))
                    .subscribe();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void triggerDeletionEmail(WorkspaceDeletionRequest request){
        try {
            var response = getAdminService().post().uri(uriBuilder -> uriBuilder.path(configProperties.getTriggerWorkspaceDeletionEmail()).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve().toBodilessEntity();
            response.doOnSuccess(success -> logger.info("Successfully send email to: " + request.getAgentEmail() + ". success: " + success))
                    .doOnError(error -> logger.info("Unsuccessfully send email to: " + request.getAgentEmail() + ". error: " + error))
                    .subscribe();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
