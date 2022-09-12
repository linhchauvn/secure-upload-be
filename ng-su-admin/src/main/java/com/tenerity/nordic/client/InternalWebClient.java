package com.tenerity.nordic.client;

import com.tenerity.nordic.client.dto.CaseDto;
import com.tenerity.nordic.client.dto.CaseManagementResponse;
import com.tenerity.nordic.client.dto.DocumentDto;
import com.tenerity.nordic.client.dto.DocumentListResponse;
import com.tenerity.nordic.dto.CustomerLoginRequest;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class InternalWebClient {
    Logger log = LoggerFactory.getLogger(InternalWebClient.class);
    @Value("${internal.caseServiceBaseUrl}")
    private String caseServiceBaseUrl;
    @Value("${internal.customerTokenCheckPath}")
    private String customerTokenCheckPath;
    @Value("${internal.customerSignicatCheckPath}")
    private String customerSignicatCheckPath;
    @Value("${internal.documentServiceBaseUrl}")
    private String documentServiceBaseUrl;
    @Value("${internal.sendResetPasswordEmailPath}")
    private String sendResetPasswordEmailPath;
    @Value("${internal.deleteCaseByClientPath}")
    private String deleteCaseByClientPath;
    @Value("${internal.deleteWorkspaceByThirdPartyPath}")
    private String deleteWorkspaceByThirdPartyPath;
    @Value("${internal.getDocumentListPath}")
    private String getDocumentListPath;
    @Value("${internal.getDocumentContentPath}")
    private String getDocumentContentPath;
    @Value("${internal.bulkUpdateDocumentPath}")
    private String bulkUpdateDocumentPath;
    @Value("${internal.uploadSignicatFilePath}")
    private String uploadSignicatFilePath;
    @Value("${internal.getCasePath}")
    private String getCasePath;

    @Value("${communication.server}")
    private String mailingServer;
    @Value("${communication.endpoint}")
    private String mailingEndpoint;

    @Value("${ngAuth.server}")
    private String ngAuthServer;
    @Value("${ngAuth.token}")
    private String ngAuthTokenEndpoint;
    @Value("${ngAuth.tenantId}")
    private String ngAuthTenantId;
    @Value("${ngAuth.clientId}")
    private String ngAuthClientId;
    @Value("${ngAuth.clientSecret}")
    private String ngAuthClientSecret;
    @Value("${ngAuth.grantType}")
    private String ngAuthGrantType;

    @Value("${basicAuth.username}")
    private String username;
    @Value("${basicAuth.password}")
    private String password;

    WebClient caseService = null;
    WebClient docService = null;

    final ConnectionProvider provider = ConnectionProvider.builder("internal-conn")
            .maxConnections(500)
            .pendingAcquireTimeout(Duration.ofSeconds(45))
            .maxIdleTime(Duration.ofSeconds(600)).build();
    final HttpClient httpClient = HttpClient.create(provider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20000)
            .responseTimeout(Duration.ofMillis(20000))
            .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(20000, TimeUnit.MILLISECONDS))
                                        .addHandlerLast(new WriteTimeoutHandler(20000, TimeUnit.MILLISECONDS)));

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

    private WebClient getDocumentService() {
        if (docService == null) {
            docService = WebClient.builder()
                    .baseUrl(documentServiceBaseUrl)
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .defaultHeaders(header -> header.setBasicAuth(username, password))
                    .build();
        }
        return docService;
    }

    public String workspaceTokenLogin(CustomerLoginRequest request) {
        try {
            var realPath = String.format(customerTokenCheckPath, request.getWorkspaceId());
            var response = getCaseService().get().uri(uriBuilder -> uriBuilder.path(realPath)
                            .queryParam("token", request.getCustomerToken())
                            .build())
                    .retrieve().bodyToMono(String.class)
                    .block();
            return response;
        } catch (Exception e) {
            log.error("[workspaceTokenLogin] ERROR: " + e.getMessage(), e);
        }
        return "";
    }

    public Boolean isAuthorizedSignicatUser(String workspaceId, String nationalId) {
        try {
            var realPath = String.format(customerSignicatCheckPath, workspaceId);
            var response = getCaseService().get().uri(uriBuilder -> uriBuilder.path(realPath)
                            .queryParam("nationalId", nationalId)
                            .build())
                    .retrieve().bodyToMono(String.class);
            return Boolean.parseBoolean(response.block());
        } catch (Exception e) {
            log.error("[isAuthorizedSignicatUser] ERROR: " + e.getMessage(), e);
        }
        return false;
    }

    public void deleteCaseByClient(String clientId) {
        try {
            var realPath = String.format(deleteCaseByClientPath, clientId);
            var response = getCaseService().delete().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().toBodilessEntity();
            response.block();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void deleteWorkspaceByThirdParty(String thirdPartyId) {
        try {
            var realPath = String.format(deleteWorkspaceByThirdPartyPath, thirdPartyId);
            var response = getCaseService().delete().uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .retrieve().toBodilessEntity();
            response.block();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public List<DocumentDto> getDocumentList(String docIds) {
        try {
            var response = getDocumentService().get()
                    .uri(uriBuilder -> uriBuilder.path(getDocumentListPath).queryParam("docIds", docIds).build())
                    .retrieve().bodyToMono(new ParameterizedTypeReference<DocumentListResponse<DocumentDto>>(){});
            return response.block().getData();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public String readDocumentContent(String documentId) {
        var realPath = String.format(getDocumentContentPath, documentId);
        var response = getDocumentService().get()
                .uri(uriBuilder -> uriBuilder.path(realPath).build())
                .retrieve().bodyToMono(Object.class)
                .block();
        Map<String,Object> map = (Map<String,Object>)response;

        return (String)map.get("data");
    }

    public void bulkUpdateDocument(String docIds, String signicatRequestId, String signicatTaskId, Boolean eSigned) {
        try {
            Map<String, Object> bodyMap = new HashMap<>();
            if (docIds != null) {
                bodyMap.put("docIds", docIds);
            }
            if (signicatRequestId != null) {
                bodyMap.put("signicatRequestId", signicatRequestId);
            }
            if (signicatTaskId != null) {
                bodyMap.put("signicatTaskId", signicatTaskId);
            }
            if (eSigned != null) {
                bodyMap.put("eSigned", eSigned);
            }

            var response = getDocumentService().put()
                    .uri(uriBuilder -> uriBuilder.path(bulkUpdateDocumentPath).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(bodyMap))
                    .retrieve()
                    .toBodilessEntity()
                    .subscribe();
            log.info("[bulkUpdateDocument] SUCCESS");
        } catch (Exception ex) {
            log.info("[bulkUpdateDocument] ERROR: " + ex.getMessage(), ex);
        }
    }

    public void uploadSignicatFile(String packagingTaskId, File convFile) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new FileSystemResource(convFile));
            String path = String.format(uploadSignicatFilePath, packagingTaskId);
            log.info(String.format("[Signicat] Start upload file to S3"));
            var response = getDocumentService().post()
                    .uri(uriBuilder -> uriBuilder.path(path).build())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Object.class);
            response.doOnSuccess(suc -> {
                        log.info("Upload Signicat file successfully. packagingTaskId=" + packagingTaskId);

                        convFile.delete();
                            })
                    .doOnError(err -> {
                        log.info("Upload Signicat file failed. packagingTaskId=" + packagingTaskId + "Reason: " + err.getMessage());
                        convFile.delete();
                    })
                    .subscribe();
        } catch (Exception ex) {
            log.info(String.format("[Signicat] ERROR %s TRACE ", ex.getMessage(), ex));
            log.error("[bulkUpdateDocument] ERROR: " + ex.getMessage(), ex);
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
}
