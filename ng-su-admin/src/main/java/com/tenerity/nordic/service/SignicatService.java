package com.tenerity.nordic.service;

import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.SignicatWebClient;
import com.tenerity.nordic.client.dto.*;
import com.tenerity.nordic.enums.Locale;
import com.tenerity.nordic.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SignicatService {
    Logger logger = LoggerFactory.getLogger(SignicatService.class);
    @Autowired
    private SignicatWebClient webClient;
    @Autowired
    private InternalWebClient internalWebClient;

    @Value("${signicat.signingRedirectUrl}")
    private String redirectUrl;

    private String redirectQueryParams = "?requestId=${requestId}&taskId=${taskId}";

    public Map<String, String> getSignicatAuthorizationUrl(String workspaceId, String locale) {
        return webClient.buildAuthorizationUrls(workspaceId, locale);
    }

    public String getSignicatSigningUrl(String docIds, String locale, String host) {
        // get need esign document
        var needEsignDocuments = internalWebClient.getDocumentList(docIds);
        // check if case is closed
        if (needEsignDocuments.size() > 0) {
            var caseId = needEsignDocuments.get(0).getCaseId();
            var caseDto = internalWebClient.getCaseById(caseId.toString());
            if (caseDto == null || "CLOSE".equals(caseDto.getStatus())) {
                return null;
            }
        }
        // upload doc to signicat
        String token = webClient.getSignToken();
        List<SignicatSigningOrderDocument> signingOrderDocuments = new ArrayList<>();
        needEsignDocuments.forEach(item -> {
            String encodedContent = internalWebClient.readDocumentContent(item.getId().toString());
            byte[] content = Base64.getDecoder().decode(encodedContent);
            var signicatDocId = webClient.uploadSignDocument(token, content);
            signingOrderDocuments.add(createSigningOrderDocument(item, signicatDocId));
        });
        // create signing order
        var caseId = needEsignDocuments.get(0).getCaseId().toString();
        SignicatSigningOrderRequest signingRequest = createSigningOrderRequest(caseId, locale, signingOrderDocuments, host);
        var signicatRes = webClient.createSigningOrder(token, signingRequest);
        if (signicatRes == null) {
            return null;
        }
        var taskId = signicatRes.getTasks().get(0).getId();
        var orderId = signicatRes.getId();
        internalWebClient.bulkUpdateDocument(docIds, orderId, taskId, null);
        return signicatRes.getTasks().get(0).getSigningUrl();
    }

    public SignicatSignOrderStatusResponse getOrderStatus(String orderId, String taskId) {
        logger.info(String.format("[Signicat-getOrderStatus] orderId = %s taskId = %s", orderId, taskId));
        String token = webClient.getSignToken();
        logger.info(String.format("[Signicat-getOrderStatus] get token %s", token));
        SignicatSignOrderStatusResponse response = null;
        try {
            response = this.getCompletedOrderStatus(token, orderId, taskId);
            if(response != null) {
                logger.info(String.format("[Signicat-getOrderStatus] get status %s", response.getTaskStatus()));
                if ("COMPLETED".equals(response.getTaskStatus())) {
                    List<String> documentIdList = response.getDocuments().stream().map(item -> item.getId()).collect(Collectors.toList());
                    internalWebClient.bulkUpdateDocument(String.join(Constants.COMMA_DELIMITER, documentIdList), null, null, true);
                    logger.info(String.format("[Signicat] Updated document status documentIdList=%s", documentIdList));
                }

                response.getDocuments().forEach(doc -> {
                    var packagingTaskId = doc.getId();
                    logger.info(String.format("[Signicat] Get doc= %s", packagingTaskId));
                    SignicatPackagingTaskStatusResponse pkgStatus = null;
                    try {
                        pkgStatus = this.getCompletedDocTask(token, orderId, packagingTaskId);
                        if (pkgStatus!= null && "COMPLETED".equals(pkgStatus.getPackagingTaskStatus())) {
                            logger.info(String.format("[Signicat] Start download file from signicat"));
                            var convFile = webClient.getPackagingTaskResult(token, orderId, packagingTaskId);
                            logger.info(String.format("[Signicat] End download file from signicat"));
                            logger.info(String.format("[Signicat] Start upload file to S3"));
                            internalWebClient.uploadSignicatFile(packagingTaskId, convFile);
                        }
                    } catch (InterruptedException e) {
                        logger.info(String.format("[Signicat] getDocStatus exception %s", e.getMessage()));
                        e.printStackTrace();
                    }
                });
            }
        } catch (InterruptedException e) {
            logger.info(String.format("[Signicat] getOrderStatus exception %s", e.getMessage()));
            e.printStackTrace();
        }
        return response;
    }

    private SignicatPackagingTaskStatusResponse getCompletedDocTask(String token, String orderId, String packagingTaskId) throws InterruptedException {
        // SignicatPackagingTaskStatusResponse pkgStatus = null;
        var pkgStatus = webClient.getPackagingTaskStatus(token, orderId, packagingTaskId);
        logger.info(String.format("[Signicat] Get docStatus= %s", pkgStatus != null ? pkgStatus.getPackagingTaskStatus() : null));
        Integer count = 0;
        while (count < 6 && (pkgStatus == null || !"COMPLETED".equals(pkgStatus.getPackagingTaskStatus()))) {
            TimeUnit.SECONDS.sleep(5);
            count++;
            pkgStatus = webClient.getPackagingTaskStatus(token, orderId, packagingTaskId);
            logger.info(String.format("[Signicat] Get docStatus: %s", pkgStatus != null ? pkgStatus.getPackagingTaskStatus() : null));
            logger.info(String.format("[Signicat] Get docStatus - times: %s", count.toString()));
        }
        return pkgStatus;
    }

    private SignicatSignOrderStatusResponse getCompletedOrderStatus(String token, String orderId, String taskId) throws InterruptedException {
        var response = webClient.getSigningOrderStatus(token, orderId, taskId);
        logger.info(String.format("[Signicat] Get orderStatus= %s", response != null ? response.getTaskStatus(): null));
        Integer count = 0;
        while (count < 6 && (response == null || !"COMPLETED".equals(response.getTaskStatus()))){
            TimeUnit.SECONDS.sleep(5);
            count++;
            response = webClient.getSigningOrderStatus(token, orderId, taskId);
            logger.info(String.format("[Signicat] Get orderStatus= %s", response != null ? response.getTaskStatus() : null));
            logger.info(String.format("[Signicat] Get orderStatus - times: %s", count.toString()));
        }
        return  response;
    }

//    public DocumentEsignContentResponse getPackagingTaskResult(String requestId, String packagingTaskId) {
//        var response = new DocumentEsignContentResponse();
//        String token = webClient.getSignToken();
//        var convFile = webClient.getPackagingTaskResult(token, requestId, packagingTaskId);
//        internalWebClient.uploadSignicatFile(packagingTaskId, convFile);
//        convFile.delete();
//        return response;
//    }

    private SignicatSigningOrderRequest createSigningOrderRequest(String caseId, String locale, List<SignicatSigningOrderDocument> requestDocs,
                                                                  String host) {
        SignicatSigningOrderRequest clientReq = new SignicatSigningOrderRequest();
        clientReq.setClientReference(caseId);

        String taskId = UUID.randomUUID().toString();
        SignicatSigningOrderTask task = new SignicatSigningOrderTask();
        task.setId(taskId);
        task.setLanguage("no".equals(locale) ? "nb" : locale);
        task.setDocuments(requestDocs);
        task.setSignatureMethods(createSignatureMethods(locale));
        String fullRedirectUrl = redirectUrl.replace("{{HOST}}", host) + redirectQueryParams;
        task.setOnTaskComplete(fullRedirectUrl);
        task.setOnTaskReject(fullRedirectUrl);
        clientReq.setTasks(Arrays.asList(task));

        var packagingTasks = new ArrayList<SignicatPackagingTask>();
        requestDocs.forEach(item -> {
            var packagingTask = new SignicatPackagingTask();
            packagingTask.setId(item.getId());
            packagingTask.setSendToArchive(true);
            packagingTask.setMethod("pades");
            var pkgTaskDoc = new SignicatPackagingTaskDocument();
            pkgTaskDoc.setTaskId(taskId);
            pkgTaskDoc.setDocumentIds(Arrays.asList(item.getId()));
            packagingTask.setDocuments(Arrays.asList(pkgTaskDoc));
            packagingTasks.add(packagingTask);
        });
        clientReq.setPackagingTasks(packagingTasks);

        return clientReq;
    }

    private List<SignicatSigningOrderMethod> createSignatureMethods(String locale) {
        var methods = new ArrayList<SignicatSigningOrderMethod>();
        var nameList = new ArrayList<String>();
        switch (Locale.valueOf(locale)) {
            case sv:
                nameList.add("sbid-sign");
                break;
            case da:
                nameList.add("nemid-sign");
                break;
            case no:
                nameList.add("nbid-sign");
                nameList.add("nbid-mobil-sign");
                break;
            case fi:
                nameList.add("ftn-sign");
                break;
        }

        nameList.forEach(item -> {
            var method = new SignicatSigningOrderMethod();
            method.setName(item);
            method.setType("SIGNED_STATEMENT");
            methods.add(method);
        });
        return methods;
    }

    private SignicatSigningOrderDocument createSigningOrderDocument(DocumentDto dto, String documentRef) {
        var document = new SignicatSigningOrderDocument();
        document.setId(dto.getId().toString());
        document.setDescription(dto.getLabel());
        document.setDocumentRef(documentRef);
        document.setAction("SIGN");
        document.setSource("SESSION");
        document.setSendResultToArchive(Boolean.TRUE);
        return document;
    }

//    private SignicatPackagingTask createSignicatPackagingTask(String taskId, List<String> documentIds) {
//        SignicatPackagingTask packagingTask = new SignicatPackagingTask();
//        packagingTask.setId(taskId);
//        packagingTask.setSendToArchive(true);
//        packagingTask.setMethod("pades");
//        var packageDocument = new SignicatPackagingTaskDocument();
//        packageDocument.setTaskId(taskId);
//        packageDocument.setDocumentIds(documentIds);
//        packagingTask.setDocuments(Arrays.asList(packageDocument));
//        return packagingTask;
//    }
}
