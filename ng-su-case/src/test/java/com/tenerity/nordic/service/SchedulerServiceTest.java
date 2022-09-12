package com.tenerity.nordic.service;

import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.UserDto;
import com.tenerity.nordic.client.dto.DocumentEmailRequest;
import com.tenerity.nordic.dto.DashboardSearchParameter;
import com.tenerity.nordic.entity.Case;
import com.tenerity.nordic.enums.CaseStatistic;
import com.tenerity.nordic.enums.CaseStatus;
import com.tenerity.nordic.repository.CaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SchedulerServiceTest {
    @Mock
    private DashboardRepository dashboardRepository;
    @Mock
    private InternalWebClient webClient;
    @InjectMocks
    private SchedulerService schedulerService;

    @Test
    void checkingDocumentStatus_happyCase() {
        var agent = new UserDto();
        agent.setId(UUID.randomUUID());
        agent.setEmailAddress("agent@email.com");
        when(webClient.getAgentById(any())).thenReturn(agent);
        var data = new Case();
        data.setId(UUID.randomUUID());
        data.setAssignedAgent(agent.getId());
        data.setCustomerEmail("data@custemail.com");
        when(dashboardRepository.searchCases(any())).thenReturn(Arrays.asList(data));

        schedulerService.checkingDocumentStatus();
        ArgumentCaptor<DocumentEmailRequest> captor = ArgumentCaptor.forClass(DocumentEmailRequest.class);
        verify(webClient, times(2)).triggerUploadEmail(captor.capture());
        assertTrue(captor.getAllValues().stream().anyMatch(item -> data.getId().toString().equals(item.getCaseId())));

    }

}
