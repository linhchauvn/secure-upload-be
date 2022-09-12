package com.tenerity.nordic.service;

import com.tenerity.nordic.client.ClientDto;
import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.UserDto;
import com.tenerity.nordic.dto.CaseDto;
import com.tenerity.nordic.dto.CaseManagementRequest;
import com.tenerity.nordic.dto.CaseSearchRequest;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CaseServiceTest {
    @Mock
    private CaseRepository caseRepository;
    @Mock
    private DashboardRepository dashboardRepository;
    @Mock
    private InternalWebClient webClient;
    @InjectMocks
    private CaseService caseService;

    @Test
    void createCase_happyCase() {
        var req = new CaseManagementRequest();
        req.setSuperOfficeId("superOfficeId");
        req.setCustomerEmail("customer@email.com");
        req.setCustomerNationalId("nationalId");
        UUID agentId = UUID.randomUUID();
        req.setAgentId(agentId.toString());
        UUID clientId = UUID.randomUUID();
        req.setClientId(clientId.toString());

        var entity = new Case();
        entity.setId(UUID.randomUUID());
        entity.setSuperOfficeID(req.getSuperOfficeId());
        entity.setCustomerEmail(req.getCustomerEmail());
        entity.setCustomerNationalId(req.getCustomerNationalId());
        entity.setAssignedAgent(agentId);
        entity.setClientId(clientId);
        entity.setStatus(CaseStatus.OPEN);
        when(caseRepository.save(any())).thenReturn(entity);

        var res = caseService.createCase(req);
        assertNotNull(res);
        assertTrue(res.getData() instanceof CaseDto);
        assertEquals(req.getSuperOfficeId(), ((CaseDto) res.getData()).getSuperOfficeId());
        assertEquals(agentId, ((CaseDto) res.getData()).getAssignedAgent().getId());
        assertEquals(clientId, ((CaseDto) res.getData()).getClientId());
    }

    @Test
    void updateCase_happyCase() {
        UUID id = UUID.randomUUID();
        var req = new CaseManagementRequest();
        req.setSuperOfficeId("updateOfficeId");

        var entity = new Case();
        entity.setId(id);
        entity.setAssignedAgent(UUID.randomUUID());
        entity.setClientId(UUID.randomUUID());
        entity.setStatus(CaseStatus.OPEN);
        when(caseRepository.getById(id)).thenReturn(entity);
        entity.setSuperOfficeID(req.getSuperOfficeId());
        when(caseRepository.save(any())).thenReturn(entity);

        var res = caseService.updateCase(id.toString(), req);
        assertNotNull(res);
        assertTrue(res.getData() instanceof CaseDto);
        assertEquals(req.getSuperOfficeId(), ((CaseDto) res.getData()).getSuperOfficeId());
    }

    @Test
    void setDocumentActionForCase_happyCase() {
        UUID id = UUID.randomUUID();
        var req = new CaseManagementRequest();
        req.setLastUpdate(LocalDateTime.now());
        req.setDocumentsExpunged(true);
        req.setNeedAgentNotification(true);

        var entity = new Case();
        entity.setId(id);
        entity.setAssignedAgent(UUID.randomUUID());
        entity.setClientId(UUID.randomUUID());
        entity.setStatus(CaseStatus.OPEN);
        when(caseRepository.getById(id)).thenReturn(entity);
        entity.setLastUpdated(req.getLastUpdate());
        entity.setDocumentsExpunged(req.getDocumentsExpunged());
        entity.setNeedAgentNotification(req.getNeedAgentNotification());
        when(caseRepository.save(any())).thenReturn(entity);

        var res = caseService.setDocumentActionForCase(id.toString(), req);
        assertNotNull(res);
        assertTrue(res.getData() instanceof CaseDto);
        assertEquals(req.getLastUpdate(), ((CaseDto) res.getData()).getLastUpdated());
        assertEquals(req.getDocumentsExpunged(), ((CaseDto) res.getData()).getDocumentsExpunged());
    }

    @Test
    void closeCase_happyCase() {
        UUID id = UUID.randomUUID();

        var entity = new Case();
        entity.setId(id);
        entity.setStatus(CaseStatus.OPEN);
        when(caseRepository.getById(id)).thenReturn(entity);

        var res = caseService.closeCase(id.toString());
        assertNotNull(res);
        assertNull(res.getMessage());
        ArgumentCaptor<Case> captor = ArgumentCaptor.forClass(Case.class);
        verify(caseRepository).save(captor.capture());
        assertEquals(CaseStatus.CLOSE, captor.getValue().getStatus());
    }

    @Test
    void deleteCaseByClient_happyCase() {
        UUID id = UUID.randomUUID();
        var entity = new Case();
        entity.setId(id);
        entity.setStatus(CaseStatus.OPEN);
        when(caseRepository.findAllByClientId(id)).thenReturn(Arrays.asList(entity));

        var res = caseService.deleteCaseByClient(id.toString());
        assertNotNull(res);
        assertNull(res.getMessage());
        verify(webClient, times(1)).deleteDocumentByCaseId(id.toString());
    }

    @Test
    void searchCase_happyCase() {
        UUID id = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        var entity = new Case();
        entity.setId(id);
        entity.setStatus(CaseStatus.OPEN);
        entity.setAssignedAgent(agentId);
        entity.setClientId(clientId);
        when(dashboardRepository.searchCases(any())).thenReturn(Arrays.asList(entity));
        var agent = new UserDto();
        agent.setId(agentId);
        agent.setEmailAddress("agentemail");
        when(webClient.getAllAgents()).thenReturn(Arrays.asList(agent));
        var client = new ClientDto();
        client.setId(clientId);
        client.setName("clientname");
        when(webClient.getAllClients()).thenReturn(Arrays.asList(client));

        var req = new CaseSearchRequest();
        req.setCaseStatistic(CaseStatistic.ALL);
        req.setPage(0);
        req.setSize(10);
        var res = caseService.searchCase(req);
        assertNotNull(res);
        assertEquals(1, res.getTotalItem());
        assertEquals(id, res.getResults().get(0).getId());
        assertEquals(agent.getEmailAddress(), res.getResults().get(0).getAssignedAgent());
        assertEquals(client.getName(), res.getResults().get(0).getClient());
    }

    @Test
    void getCasesStatistics_happyCase() {
        var newCase = new Case();
        newCase.setStatus(CaseStatus.OPEN);
        var resolveCase = new Case();
        resolveCase.setStatus(CaseStatus.CLOSE);
        var twentyFourCase = new Case();
        twentyFourCase.setStatus(CaseStatus.OPEN);
        twentyFourCase.setNeedAgentNotification(true);
        twentyFourCase.setLastUpdated(LocalDateTime.now().minusHours(25));
        var seventyTwoCase = new Case();
        seventyTwoCase.setStatus(CaseStatus.OPEN);
        seventyTwoCase.setNeedAgentNotification(true);
        var seventyTwoHoursAgo = LocalDateTime.now().minusHours(73);
        if (CaseService.excludeWeekendDays.contains(LocalDateTime.now().getDayOfWeek())) {
            seventyTwoHoursAgo = seventyTwoHoursAgo.minusHours(24);
        }
        seventyTwoCase.setLastUpdated(seventyTwoHoursAgo);
        when(caseRepository.findAll()).thenReturn(new ArrayList<Case>(Arrays.asList(newCase, resolveCase, twentyFourCase, seventyTwoCase)));

        var res = caseService.getCasesStatistics();
        assertNotNull(res);
        assertEquals(4, res.getAllCases());
        assertEquals(1, res.getNewCases());
        assertEquals(1, res.getResolvedCases());
        assertEquals(1, res.getOpenCasesExceed24Hours());
        assertEquals(1, res.getOpenCasesExceed72Hours());
    }

    @Test
    void getCaseById_happyCase() {
        UUID id = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        var agent = new UserDto();
        agent.setId(agentId);
        agent.setEmailAddress("agentemail");
        when(webClient.getAgentById(agentId)).thenReturn(agent);
        var client = new ClientDto();
        client.setId(clientId);
        client.setName("clientname");
        when(webClient.getClientById(clientId)).thenReturn(client);
        var entity = new Case();
        entity.setId(id);
        entity.setAssignedAgent(agentId);
        entity.setClientId(clientId);
        entity.setStatus(CaseStatus.OPEN);
        when(caseRepository.getById(id)).thenReturn(entity);

        var res = caseService.getCaseById(id.toString());
        assertNotNull(res);
        assertTrue(res.getData() instanceof CaseDto);
        assertEquals(agent.getEmailAddress(), ((CaseDto) res.getData()).getAssignedAgent().getEmailAddress());
        assertEquals(client.getName(), ((CaseDto) res.getData()).getClient().getName());
    }
}
