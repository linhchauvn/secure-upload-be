package com.tenerity.nordic.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tenerity.nordic.client.dto.CommunicationRequest;
import com.tenerity.nordic.client.dto.NgAuthLoginResponse;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class NgAuthWebClient {
    Logger logger = LoggerFactory.getLogger(NgAuthWebClient.class);

    @Value("${ngAuth.server}")
    private String ngAuthServer;
    @Value("${ngAuth.token}")
    private String ngAuthTokenEndpoint;
    @Value("${ngAuth.login-api}")
    private String ngAuthLoginEndpoint;
    @Value("${ngAuth.create-user}")
    private String ngAuthCreateUserEndpoint;
    @Value("${ngAuth.retrieve-user}")
    private String ngAuthRetrieveUserEndpoint;
    @Value("${ngAuth.update-user}")
    private String ngAuthUpdateUsernameEndpoint;
    @Value("${ngAuth.update-password}")
    private String ngAuthUpdatePasswordEndpoint;
    @Value("${ngAuth.delete-user}")
    private String ngAuthDeleteUserEndpoint;
    @Value("${ngAuth.keycloak-server}")
    private String keycloakServer;
    @Value("${ngAuth.update-username}")
    private String keycloakUpdateUsernameEndpoint;

    @Value("${communication.server}")
    private String mailingServer;
    @Value("${communication.endpoint}")
    private String mailingEndpoint;

    @Value("${ngAuth.tenantId}")
    private String ngAuthTenantId;
    @Value("${ngAuth.clientId}")
    private String ngAuthClientId;
    @Value("${ngAuth.clientSecret}")
    private String ngAuthClientSecret;
    @Value("${ngAuth.grantType}")
    private String ngAuthGrantType;

    private static final String TENANT_ID = "tenant-id";
    private static final String X_CORRELATION_ID = "x-correlation-id";
    WebClient ngAuthService = null;
    WebClient mailingService = null;

    final ConnectionProvider provider = ConnectionProvider.builder("ngauth-conn")
            .maxConnections(500)
            .pendingAcquireTimeout(Duration.ofSeconds(45))
            .maxIdleTime(Duration.ofSeconds(600)).build();
    final HttpClient httpClient = HttpClient.create(provider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
            .responseTimeout(Duration.ofMillis(30000))
            .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(30000, TimeUnit.MILLISECONDS))
                                        .addHandlerLast(new WriteTimeoutHandler(30000, TimeUnit.MILLISECONDS)));

    private WebClient getNgAuthService() {
        if (ngAuthService == null) {
            ngAuthService = WebClient.builder()
                    .baseUrl(ngAuthServer)
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();
        }
        return ngAuthService;
    }

    private WebClient getMailingService() {
        if (mailingService == null) {
            mailingService = WebClient.builder()
                    .baseUrl(mailingServer)
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();
        }
        return mailingService;
    }

    public String getNgAuthToken() {
        try{
            var response = getNgAuthService().post()
                    .uri(uriBuilder -> uriBuilder.path(ngAuthTokenEndpoint).build())
                    .header(TENANT_ID, ngAuthTenantId)
                    .header(X_CORRELATION_ID, UUID.randomUUID().toString())
                    .header(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters
                            .fromFormData("client_id", ngAuthClientId)
                            .with("client_secret", ngAuthClientSecret)
                            .with("grant_type", ngAuthGrantType))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            Map<String, Object> map = (Map<String, Object>) response;
            String token = map.get("access_token").toString();
            return token;
        } catch (Exception ex) {
            logger.error("[getNgAuthToken] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    public NgAuthLoginResponse ngAuthLogin(String username, String password, String ngAuthToken) {
        try {
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("clientId", ngAuthClientId);
            bodyMap.put("clientSecret", ngAuthClientSecret);
            bodyMap.put("username", username);
            bodyMap.put("password", password);

            var response = getNgAuthService().post()
                    .uri(uriBuilder -> uriBuilder.path(ngAuthLoginEndpoint).build())
                    .header(TENANT_ID, ngAuthTenantId)
                    .header(X_CORRELATION_ID, UUID.randomUUID().toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ngAuthToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(bodyMap))
                    .retrieve()
                    .bodyToMono(NgAuthLoginResponse.class)
                    .block();
            return response;
        } catch (Exception ex) {
            logger.error("[ngAuthLogin] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    public String ngAuthCreateUser(String email, String username, String password, String id, String ngAuthToken, boolean isThirdparty) {
        try {
            String body = ngAuthCreateUserBody(email, username, password, id, isThirdparty);

            var response = getNgAuthService().post()
                    .uri(uriBuilder -> uriBuilder.path(ngAuthCreateUserEndpoint).build())
                    .header(X_CORRELATION_ID, UUID.randomUUID().toString())
                    .header(TENANT_ID, ngAuthTenantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ngAuthToken)
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

            Map<String, Object> map = (Map<String, Object>) response;
            Object authUserId = map.get("id");
            if (authUserId != null && authUserId instanceof String) {
                return (String)authUserId;
            }
            return "";
        } catch (Exception ex) {
            logger.error("[ngAuthLogin] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    private String ngAuthCreateUserBody(String email, String username, String password, String id, boolean isThirdparty) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        if(!isThirdparty) {
            root.put("email", email);
        }
        root.put("enabled", "true");
        root.put("username", username);

        ArrayNode credsArrayNode = mapper.createArrayNode();
        ObjectNode credentials = mapper.createObjectNode();
        credentials.put("temporary", false);
        credentials.put("type", "password");
        credentials.put("value", password);
        credsArrayNode.add(credentials);
        root.set("credentials", credsArrayNode);

        ObjectNode attributes = mapper.createObjectNode();
        attributes.put("member_id", id);
        root.set("attributes", attributes);
        try {
            return mapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            logger.error("[ngAuthCreateUserBody ERROR] " + e.getMessage(), e);
            return null;
        }
    }

    public Map<String, Object> ngAuthRetrieveUser(String memberId, String ngAuthToken) {
        try {
            String realPath = String.format(ngAuthRetrieveUserEndpoint, memberId);
            var response = getNgAuthService().get()
                    .uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .header(X_CORRELATION_ID, UUID.randomUUID().toString())
                    .header(TENANT_ID, ngAuthTenantId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ngAuthToken)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

            Map<String, Object> map = (Map<String, Object>) response;
            return map;
        } catch (Exception ex) {
            logger.error("[ngAuthRetrieveUser] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    public boolean ngAuthUpdatePassword(String username, String newPassword, String ngAuthToken) {
        try {
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("username", username);
            bodyMap.put("password", newPassword);

            var response = getNgAuthService().put()
                    .uri(uriBuilder -> uriBuilder.path(ngAuthUpdatePasswordEndpoint).build())
                    .header(X_CORRELATION_ID, UUID.randomUUID().toString())
                    .header(TENANT_ID, ngAuthTenantId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ngAuthToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(bodyMap))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

            Map<String, Object> map = (Map<String, Object>) response;
            var ngAuthStatus = map.get("isPasswordResetSuccessful");
            if (ngAuthStatus != null && ngAuthStatus instanceof Boolean) {
                return (Boolean)ngAuthStatus;
            }
        } catch (Exception ex) {
            logger.error("[ngAuthUpdateUser] ERROR: " + ex.getMessage(), ex);
            throw ex;
        }
        return false;
    }

    public boolean ngAuthUpdateUsername(String memberId, String username, String ngAuthToken) {
        try {
            var keycloakUserMap = ngAuthRetrieveUser(memberId, ngAuthToken);
            var keycloakUserId = keycloakUserMap.get("id");

            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("username", username);
            String realPath = String.format(keycloakUpdateUsernameEndpoint, keycloakUserId);
            var response = getNgAuthService().put()
                    .uri(new URI(keycloakServer + realPath))
                    .header(X_CORRELATION_ID, UUID.randomUUID().toString())
                    .header(TENANT_ID, ngAuthTenantId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ngAuthToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(bodyMap))
                    .retrieve()
                    .toEntity(Object.class)
                    .block();

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception ex) {
            logger.error("[ngAuthUpdateUsername] ERROR: " + ex.getMessage(), ex);
            return false;
        }
    }

    public String ngAuthDeleteUser(String memberId, String ngAuthToken) {
        try {
            String realPath = String.format(ngAuthDeleteUserEndpoint, memberId);
            var response = getNgAuthService().delete()
                    .uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .header(X_CORRELATION_ID, UUID.randomUUID().toString())
                    .header(TENANT_ID, ngAuthTenantId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + ngAuthToken)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

            Map<String, Object> map = (Map<String, Object>) response;
            return (String) map.get("message");
        } catch (Exception ex) {
            logger.error("[ngAuthDeleteUser] ERROR: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Boolean sendingEmail(CommunicationRequest request) {
        try {
            String token = getNgAuthToken();
            if (token == null) {
                logger.info("[sendingEmail-ERROR]: Cannot get getNgAuthToken");
                return false;
            }
            logger.info(String.format("[sendingEmail-Before] CorrelationId: %s. Payload: %s", request.getCorrelationId(), request));
            var response = getMailingService().post()
                    .uri(uriBuilder -> uriBuilder.path(mailingEndpoint).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(TENANT_ID, request.getTenantId())
                    .header(X_CORRELATION_ID, request.getCorrelationId())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer "+ token)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(Duration.ofSeconds(60));

            logger.info(String.format("[sendingEmail-After] CorrelationId: %s. RES: %s", request.getCorrelationId(), response));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response);
            String commStatus = jsonNode.at("/commDetails/commStatus").asText();
            if ("ACKNOWLEDGED".equalsIgnoreCase(commStatus)) { // ACKNOWLEDGED = send email successfully
                return true;
            }
            String message = jsonNode.at("/commDetails/statusMessage").asText();
            logger.info(String.format("[sendingEmail-ERROR] CorrelationId: %s. Status: %s. Message: %s", request.getCorrelationId(), commStatus, message));
        } catch (Exception e) {
            logger.info(String.format("[sendingEmail-ERROR] CorrelationId: %s. Err message: ", request.getCorrelationId()) + e.getMessage(), e);
        }
        return false;
    }

}
