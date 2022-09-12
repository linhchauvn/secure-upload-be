package com.tenerity.nordic.service;

import com.tenerity.nordic.client.DocumentDto;
import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.dto.DocumentEmailRequest;
import com.tenerity.nordic.dto.DashboardSearchParameter;
import com.tenerity.nordic.entity.Case;
import com.tenerity.nordic.enums.CaseStatistic;
import com.tenerity.nordic.enums.CaseStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

@Service
public class SchedulerService {
    Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private InternalWebClient webClient;


//    @Scheduled(fixedDelay = 15, initialDelay = 1, timeUnit = TimeUnit.MINUTES)
    @Scheduled(cron = "@daily")
    public void checkingDocumentStatus() {
        logger.info("---CRON JOB checkingDocumentStatus START RUNNING----");
        var param24 = new DashboardSearchParameter();
        param24.setCaseStatistic(CaseStatistic.OPEN_CASE_EXCEED_24H);
        param24.setStatus(CaseStatus.OPEN);
        var data = dashboardRepository.searchCases(param24);
        logger.info(String.format("---Found %d case exceed 24 hours----", data.size()));
        var param72 = new DashboardSearchParameter();
        param72.setCaseStatistic(CaseStatistic.OPEN_CASE_EXCEED_72H);
        param72.setStatus(CaseStatus.OPEN);
        var data2 = dashboardRepository.searchCases(param72);
        logger.info(String.format("---Found %d case exceed 72 hours----", data2.size()));
        for(Case item : data) {
            sendEmailRequest(item, "Secure_Upload_Exceeded_24_hours");
        }
        for(Case item : data2) {
            sendEmailRequest(item, "Secure_Upload_Exceeded_72_hours");
        }
        logger.info("---CRON JOB checkingDocumentStatus FINISH RUNNING----");
    }

    private void sendEmailRequest(Case entity, String templateName) {
        var agent = webClient.getAgentById(entity.getAssignedAgent());
        if (agent == null || StringUtils.isBlank(agent.getEmailAddress())) {
            logger.debug(String.format("Cannot find agent by id %s. Skipped!", entity.getAssignedAgent()));
            return;
        }
        var request = new DocumentEmailRequest();
        String agentEmail = agent.getEmailAddress();
        String customerEmail = entity.getCustomerEmail();
        List<DocumentEmailRequest.FileData> fileDataList = new ArrayList<>();
        List<DocumentDto> docs = webClient.getDocumentByCaseId(entity.getId().toString());
        for( var doc: docs){
            if(!doc.getMarkAsRead()) {
                var fileData = request.new FileData();
                fileData.setMarkasread(doc.getMarkAsRead() != null && doc.getMarkAsRead());
                fileData.setFilename(String.format("%s [%s]", doc.getLabel(), formatDateTime(doc.getUploadTime())));
                fileDataList.add(fileData);
            }
        }
        request.setAgentEmail(agentEmail);
        request.setCustomerEmail(customerEmail);
        request.setFileData(fileDataList);
        request.setCaseId(entity.getId().toString());
        request.setId(entity.getSuperOfficeID());
        request.setTemplateName(templateName);
        webClient.triggerUploadEmail(request);
    }

    private String formatDateTime(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        var pattern = new DateTimeFormatterBuilder().appendPattern("MMM dd, yyyy - hh:mm a").toFormatter();
        return time.format(pattern);
    }

}

