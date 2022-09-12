package com.tenerity.nordic.client;

import com.tenerity.nordic.client.dto.SignicatPackagingTaskStatusResponse;
import com.tenerity.nordic.client.dto.SignicatSignOrderStatusResponse;
import com.tenerity.nordic.client.dto.SignicatSigningOrderRequest;
import com.tenerity.nordic.client.dto.SignicatSigningOrderResponse;
import com.tenerity.nordic.util.ConfigProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class SignicatWebClient {
    Logger logger = LoggerFactory.getLogger(SignicatWebClient.class);

    @Value("${signicat.baseUrl}")
    private String baseUrl;
    @Value("${signicat.authorizePath}")
    private String authorizePath;
    @Value("${signicat.getTokenPath}")
    private String getTokenPath;
    @Value("${signicat.userInfoPath}")
    private String userInfoPath;
    @Value("${signicat.uploadDocPath}")
    private String uploadDocPath;
    @Value("${signicat.createSignOrderPath}")
    private String createSignOrderPath;
    @Value("${signicat.getSignOrderStatusPath}")
    private String getSignOrderStatusPath;
    @Value("${signicat.getPackagingTaskStatusPath}")
    private String getPackagingTaskStatusPath;
    @Value("${signicat.getPackagingDocPath}")
    private String getPackagingDocPath;

    @Value("${signicat.clientId}")
    private String clientId;
    @Value("${signicat.clientSecret}")
    private String clientSecret;
    @Value("${signicat.redirectUrl}")
    private String redirectUrl;

    @Autowired
    private ConfigProperties configProperties;

    WebClient signicatService = null;

    final ConnectionProvider provider = ConnectionProvider.builder("signicat-conn")
            .maxConnections(500)
            .pendingAcquireTimeout(Duration.ofSeconds(45))
            .maxIdleTime(Duration.ofSeconds(600)).build();
    final HttpClient httpClient = HttpClient.create(provider)
            .followRedirect(true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
            .responseTimeout(Duration.ofMillis(30000))
            .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(30000, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(30000, TimeUnit.MILLISECONDS)));

    private WebClient getSignicatService() {
        if (signicatService == null) {
            signicatService = WebClient.builder()
                    .baseUrl(baseUrl)
                    .clientConnector(new ReactorClientHttpConnector(httpClient.compress(true)))
                    .build();
        }
        return signicatService;
    }

    public Map<String, String> buildAuthorizationUrls(String workspaceId, String locale) {
        Map<String, String> signicatUrlMap = new LinkedHashMap<>();
        Map<String,String> signicatMethodMap = configProperties.getSignicatMethodName();
        if (StringUtils.isNotBlank(locale)) {
            var filterMap = signicatMethodMap.entrySet()
                    .stream()
                    .filter(entry -> locale.equalsIgnoreCase(entry.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            signicatMethodMap = filterMap;
        }
        signicatMethodMap.forEach((k, v) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(baseUrl)
                    .append(authorizePath)
                    .append("?response_type=code&scope=openid+profile+signicat.national_id")
                    .append("&client_id=").append(clientId)
                    .append("&redirect_uri=").append(redirectUrl)
                    .append("&state=").append(workspaceId)
                    .append("&ui_locales=").append(v)
                    .append("&acr_values=urn:signicat:oidc:method:").append(k);
            signicatUrlMap.put(k, sb.toString());
        });

        return signicatUrlMap;
    }

    public String getAuthToken(String code) {
        try{
            var response = getSignicatService().post()
                    .uri(uriBuilder -> uriBuilder.path(getTokenPath).build())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + buildBase64BasicAuth())
                    .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                            .with("code", code)
                            .with("redirect_uri", redirectUrl))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            Map<String, Object> map = (Map<String, Object>) response;
            String token = map.get("access_token").toString();
            return token;
        } catch (Exception ex) {
            logger.error("[getToken] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    public String getUserNationalId(String accessToken) {
        try{
            var response = getSignicatService().post()
                    .uri(uriBuilder -> uriBuilder.path(userInfoPath).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            Map<String, Object> map = (Map<String, Object>) response;
            String nationalId = map.get("signicat.national_id").toString();
            return nationalId;
        } catch (Exception ex) {
            logger.error("[getUserNationalId] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    public String getSignToken() {
        try{
            var response = getSignicatService().post()
                    .uri(uriBuilder -> uriBuilder.path(getTokenPath).build())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + buildBase64BasicAuth())
                    .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                            .with("scope", "client.signature"))
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            Map<String, Object> map = (Map<String, Object>) response;
            String token = map.get("access_token").toString();
            return token;
        } catch (Exception ex) {
            logger.error("[getSignToken] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    public String uploadSignDocument(String accessToken, byte[] content) {
        try{
            var response = getSignicatService().post()
                    .uri(uriBuilder -> uriBuilder.path(uploadDocPath).build())
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .bodyValue(content)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            Map<String, Object> map = (Map<String, Object>) response;
            String documentId = map.get("documentId").toString();
            return documentId;
        } catch (Exception ex) {
            logger.error("[uploadSignDocument] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    public SignicatSigningOrderResponse createSigningOrder(String accessToken, SignicatSigningOrderRequest request) {
        try{
            var response = getSignicatService().post()
                    .uri(uriBuilder -> uriBuilder.path(createSignOrderPath).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<SignicatSigningOrderResponse>(){})
                    .block();
            return response;
        } catch (Exception ex) {
            logger.error("[createSigningOrder] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    public SignicatSignOrderStatusResponse getSigningOrderStatus(String accessToken, String orderId, String taskId) {
        try{
            var realPath = String.format(getSignOrderStatusPath, orderId, taskId);
            var response = getSignicatService().get()
                    .uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(HttpStatus::isError, res -> res.bodyToMono(String.class) // error body as String or other class
                            .flatMap(error -> {
                                return Mono.error(new RuntimeException(error));
                            })) // throw a functional exception
                    .bodyToMono(new ParameterizedTypeReference<SignicatSignOrderStatusResponse>(){})
                    .block();
            return response;
        } catch (Exception ex) {
            logger.info(String.format("[getSigningOrderStatus] ERROR: %s Trace: %s", ex.getMessage()), ex);
            logger.error("[getSigningOrderStatus] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    public SignicatPackagingTaskStatusResponse getPackagingTaskStatus(String accessToken, String requestId, String packageTaskId) {
        try{
            var realPath = String.format(getPackagingTaskStatusPath, requestId, packageTaskId);
            var response = getSignicatService().get()
                    .uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(HttpStatus::isError, res -> res.bodyToMono(String.class) // error body as String or other class
                            .flatMap(error -> {
                                return Mono.error(new RuntimeException(error));
                            })) // throw a functional exception
                    .bodyToMono(new ParameterizedTypeReference<SignicatPackagingTaskStatusResponse>(){})
                    .block();
            return response;
        } catch (Exception ex) {
            logger.info(String.format("[getPackagingTaskStatus] ERROR: %s TRACE: %s", ex.getMessage(), ex));
            logger.error("[getPackagingTaskStatus] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    public File getPackagingTaskResult(String accessToken, String requestId, String packageTaskId) {
        try{
            var realPath = String.format(getPackagingDocPath, requestId, packageTaskId);
            logger.info(String.format("[Signicat] Get file from: %s", realPath));
            var dataBufferFlux = getSignicatService().get()
                    .uri(uriBuilder -> uriBuilder.path(realPath).build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .toEntityFlux(DataBuffer.class)
                    .block(Duration.ofMinutes(1));

            do {
                logger.info(String.format("[Signicat] Do... header size: %s", dataBufferFlux.getHeaders().get(HttpHeaders.LOCATION).size()));
                if (dataBufferFlux.getHeaders().get(HttpHeaders.LOCATION).size() == 1) {
                    var currentURI = new URI(dataBufferFlux.getHeaders().get(HttpHeaders.LOCATION).get(0));
                    logger.info(String.format("[Signicat] Get file from: %s", currentURI));
                    dataBufferFlux = getSignicatService().get().uri(currentURI)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                            .retrieve()
                            .toEntityFlux(DataBuffer.class)
                            .block(Duration.ofMinutes(1));
                }
                logger.info(String.format("[Signicat] while: %s", dataBufferFlux.getStatusCode().is3xxRedirection()));
            } while (dataBufferFlux.getStatusCode().is3xxRedirection());

//            var headers = dataBufferFlux.getHeaders();
//            var byteArray = DataBufferUtils.join(dataBufferFlux.getBody())
//                    .map(dataBuffer -> {
//                       byte[] bytes = new byte[dataBuffer.readableByteCount()];
//                        dataBuffer.read(bytes);
//                        DataBufferUtils.release(dataBuffer);
//                        return bytes;
//                    }).block(Duration.ofMillis(60000));

            File convFile = new File("tempfile" + System.currentTimeMillis() + ".pdf");
            FileOutputStream fos = new FileOutputStream(convFile);
            DataBufferUtils.write(dataBufferFlux.getBody(), fos).share().blockLast(Duration.ofMillis(80000));
            logger.info(String.format("[Signicat] Download success"));
            fos.close();
            return convFile;
        } catch (Exception ex) {
            logger.info(String.format("[Signicat] Download file error: %s TRACE: %s", ex.getMessage(), ex));
            logger.error("[getPackagingTaskResult] ERROR: " + ex.getMessage(), ex);
            return null;
        }
    }

    private String buildBase64BasicAuth() {
        String input = clientId + ":" + clientSecret;
//        String input = "demo-preprod" + ":" + "mqZ-_75-f2wNsiQTONb7On4aAZ7zc218mrRVk1oufa8";
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}

