package com.tenerity.nordic.service;

import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.SignicatWebClient;
import com.tenerity.nordic.client.dto.CaseDto;
import com.tenerity.nordic.client.dto.DocumentDto;
import com.tenerity.nordic.client.dto.SignicatPackagingTaskStatusResponse;
import com.tenerity.nordic.client.dto.SignicatSignOrderStatusDocument;
import com.tenerity.nordic.client.dto.SignicatSignOrderStatusResponse;
import com.tenerity.nordic.client.dto.SignicatSigningOrderResponse;
import com.tenerity.nordic.client.dto.SignicatSigningOrderTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SignicatServiceTest {

    @Mock
    private SignicatWebClient webClient;
    @Mock
    private InternalWebClient internalWebClient;
    @InjectMocks
    private SignicatService signicatService;

    @Test
    void getSignicatAuthorizationUrl_happyCase() {
        String id = "febc7120-00fa-47b9-8668-653c9c628698";
        Map<String,String> map = new HashMap<>();
        map.put("key", "value");
        when(webClient.buildAuthorizationUrls(id, null)).thenReturn(map);

        var res = signicatService.getSignicatAuthorizationUrl(id, null);
        assertNotNull(res);
        assertEquals("value", res.get("key"));
    }

    @Test
    void getSignicatSigningUrl_happyCase() {
        String id = "febc7120-00fa-47b9-8668-653c9c628698";
        UUID caseId = UUID.randomUUID();
        String locale = "no";
        String encodedContent = "encodedContent";
        String signicatDocId = "signicatDocId";
        String signingUrl = "signingUrl";
        var doc = new DocumentDto();
        doc.setId(UUID.fromString(id));
        doc.setCaseId(caseId);
        when(internalWebClient.getDocumentList(id)).thenReturn(Arrays.asList(doc));
        when(internalWebClient.readDocumentContent(id)).thenReturn(encodedContent);
        when(webClient.uploadSignDocument(any(), any())).thenReturn(signicatDocId);
        var caseDto = new CaseDto();
        caseDto.setId(caseId);
        caseDto.setStatus("OPEN");
        when(internalWebClient.getCaseById(doc.getCaseId().toString())).thenReturn(caseDto);
        var orderRes = new SignicatSigningOrderResponse();
        SignicatSigningOrderTask task = new SignicatSigningOrderTask();
        task.setSigningUrl(signingUrl);
        orderRes.setTasks(Arrays.asList(task));
        when(webClient.createSigningOrder(any(), any())).thenReturn(orderRes);

        var res = signicatService.getSignicatSigningUrl(id, locale, "");
        assertEquals(signingUrl, res);
    }

    @Test
    void getOrderStatus_happyCase() {
        String orderId = "orderId";
        String taskId = "taskId";
        String taskStatus = "COMPLETED";
        var statusRes = new SignicatSignOrderStatusResponse();
        statusRes.setTaskStatus(taskStatus);
        var statusDoc = new SignicatSignOrderStatusDocument();
        statusDoc.setId("id");
        statusRes.setDocuments(Arrays.asList(statusDoc));
        when(webClient.getSigningOrderStatus(null, orderId, taskId)).thenReturn(statusRes);
        var pkgSttResponse = new SignicatPackagingTaskStatusResponse();
        pkgSttResponse.setPackagingTaskStatus(taskStatus);
        when(webClient.getPackagingTaskStatus(any(), any(), any())).thenReturn(pkgSttResponse);
        File testFile = new File("temp");
        when(webClient.getPackagingTaskResult(any(), any(), any())).thenReturn(testFile);
        var res  = signicatService.getOrderStatus(orderId, taskId);
        assertNotNull(res);
        assertEquals(taskStatus, res.getTaskStatus());

    }
}
