package com.tenerity.nordic.service;

import com.tenerity.nordic.client.ClientDto;
import com.tenerity.nordic.client.DocumentDto;
import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.OrganizationDto;
import com.tenerity.nordic.client.UserDto;
import com.tenerity.nordic.client.dto.UserCreateNotificationRequest;
import com.tenerity.nordic.client.dto.WorkspaceDeletionRequest;
import com.tenerity.nordic.dto.WorkspaceDocumentRequest;
import com.tenerity.nordic.dto.WorkspaceDto;
import com.tenerity.nordic.dto.WorkspaceManagementRequest;
import com.tenerity.nordic.entity.Case;
import com.tenerity.nordic.entity.Workspace;
import com.tenerity.nordic.entity.WorkspaceDocument;
import com.tenerity.nordic.entity.WorkspaceDocumentPK;
import com.tenerity.nordic.enums.CaseStatus;
import com.tenerity.nordic.repository.CaseRepository;
import com.tenerity.nordic.repository.WorkspaceDocumentRepository;
import com.tenerity.nordic.repository.WorkspaceRepository;
import com.tenerity.nordic.util.ConfigProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkspaceServiceTest {
    @Mock
    private CaseRepository caseRepository;
    @Mock
    private WorkspaceRepository workspaceRepository;
    @Mock
    private WorkspaceDocumentRepository workspaceDocumentRepository;
    @Mock
    private InternalWebClient webClient;
    @Mock
    private ConfigProperties configProperties;
    @Mock
    private AuditService auditService;
    @InjectMocks
    private WorkspaceService workspaceService;


    @Test
    void createWorkspace_happyCase() {
        UUID caseId = UUID.randomUUID();
        UUID thirdPartyId = UUID.randomUUID();
        var casee = new Case();
        casee.setId(caseId);
        when(caseRepository.findById(caseId)).thenReturn(Optional.of(casee));
        var workspace = new Workspace();
        workspace.setCasee(casee);
        workspace.setLabel("label");
        workspace.setThirdPartyId(thirdPartyId);
        when(workspaceRepository.save(any())).thenReturn(workspace);

        var req = new WorkspaceManagementRequest();
        req.setCaseId(caseId.toString());
        req.setLabel("label");
        req.setThirdPartyId(thirdPartyId.toString());
        var res = workspaceService.createWorkspace(req);
        assertNotNull(res);
        assertEquals(workspace.getLabel(), ((WorkspaceDto)res.getData()).getLabel());
    }

    @Test
    void getWorkspaceById_happyCase() {
        String authToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                "eyJtZW1iZXJfaWQiOiIzZGUwNjFmMi1hMzkyLTQ0ODItOWIxYy0zODk5MGZhMDc4NTUiLCJzdWIiOiJzdGF0aWMtand0IiwiaXNzIjoic2VjdXJlLnVwbG9hZCIsInByZWZlcnJlZF91c2VybmFtZSI6IkNVU1RPTUVSIiwiZXhwIjoxNjQ0NDYzMjIwfQ." +
                "Q4rzj-USviykOnLKqUOSa7gxLsbHPlx_mYgfza9Um3c";
        UUID id = UUID.fromString("3de061f2-a392-4482-9b1c-38990fa07855");
        UUID caseId = UUID.randomUUID();
        UUID thirdPartyId = UUID.randomUUID();
        var casee = new Case();
        casee.setId(caseId);
        var workspace = new Workspace();
        workspace.setId(id);
        workspace.setCasee(casee);
        workspace.setLabel("label");
        workspace.setThirdPartyId(thirdPartyId);
        when(workspaceRepository.getById(id)).thenReturn(workspace);
        when(workspaceDocumentRepository.findAllByWorkspaceId(id)).thenReturn(Collections.emptyList());
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var res = workspaceService.getWorkspaceById(id.toString(), authToken, "127.0.0.0");
        assertNotNull(res);
        assertEquals(id, ((WorkspaceDto)res.getData()).getId());
        assertEquals(workspace.getLabel(), ((WorkspaceDto)res.getData()).getLabel());
    }

    @Test
    void deleteWorkspace_happyCase() {
        var id = UUID.randomUUID();
        var workspace = new Workspace();
        workspace.setId(id);
        when(workspaceRepository.getById(id)).thenReturn(workspace);
        var res = workspaceService.deleteWorkspace(id.toString());
        assertNotNull(res);
        assertNull(res.getMessage());
    }

    @Test
    void requestDeleteWorkspace_happyCase() {
        var casee = new Case();
        var caseId = UUID.randomUUID();
        casee.setId(caseId);
        var agentId = UUID.randomUUID();
        casee.setAssignedAgent(agentId);
        var clientId = UUID.randomUUID();
        casee.setClientId(clientId);
        var workspace = new Workspace();
        var id = UUID.randomUUID();
        workspace.setId(id);
        workspace.setCasee(casee);
        workspace.setLabel("label");
        workspace.setThirdPartyId(UUID.randomUUID());
        when(workspaceRepository.findById(id)).thenReturn(Optional.of(workspace));
        var agent = new UserDto();
        agent.setId(agentId);
        agent.setEmailAddress("agentemail");
        when(webClient.getAgentById(agentId)).thenReturn(agent);
        var client = new ClientDto();
        client.setId(clientId);
        client.setName("clientname");
        when(webClient.getClientById(clientId)).thenReturn(client);
        when(configProperties.getWorkspaceUrl()).thenReturn("localhost:8082/workspace-service/%s");
        when(configProperties.getCaseUrl()).thenReturn("localhost:8082/workspace-service/%s");

        var res = workspaceService.requestDeleteWorkspace(id.toString());
        assertNotNull(res);
        ArgumentCaptor<WorkspaceDeletionRequest> captor = ArgumentCaptor.forClass(WorkspaceDeletionRequest.class);
        verify(webClient).triggerDeletionEmail(captor.capture());
        assertEquals(workspace.getLabel(), captor.getValue().getWorkspaceLabel());

        ArgumentCaptor<UserCreateNotificationRequest> notificationCaptor = ArgumentCaptor.forClass(UserCreateNotificationRequest.class);
        verify(webClient).createNotification(notificationCaptor.capture());
        assertEquals("DELETE", notificationCaptor.getValue().getActionType());
    }

    @Test
    void deleteWorkspaceByThirdPartyId_happyCase() {
        var id = UUID.randomUUID();
        var workspace = new Workspace();
        workspace.setId(UUID.randomUUID());
        workspace.setLabel("label");
        workspace.setThirdPartyId(id);
        when(workspaceRepository.findAllByThirdPartyId(id)).thenReturn(Arrays.asList(workspace));
        var res = workspaceService.deleteWorkspaceByThirdPartyId(id.toString());
        assertNotNull(res);
        ArgumentCaptor<List<Workspace>> captor = ArgumentCaptor.forClass(List.class);
        verify(workspaceRepository).deleteAll(captor.capture());
        assertEquals(workspace.getId(), captor.getValue().get(0).getId());
    }

    @Test
    void addDocuments_happyCase() {
        var token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIzVkR3M3N2eENPdUVENHlMRHNtLXloMEp5aHAyakg1eEJVajFnSHZiZldFIn0." +
                "eyJqdGkiOiJjNzhiZjdmMy01YzZlLTQzNWMtODRiZi0wNDA5NmNmMDcyMWEiLCJleHAiOjE2NDIzNTUwMzAsIm5iZiI6MCwiaWF0IjoxNjQyMzE5MDMwLCJpc3M" +
                "iOiJodHRwczovL3BsYXRmb3JtLWtleWNsb2FrLmludC5kZXYuYWZmaW5pb25zZXJ2aWNlcy5jb20vYXV0aC9yZWFsbXMvc2VjdXJlLXVwbG9hZCIsImF1ZCI6ImF" +
                "jY291bnQiLCJzdWIiOiI5MzhiNjFkNi01MzE0LTQyYjYtYTIwMC1lM2Q3YWJjZjVmNGEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi1zZXJ2aWNlIiwiYXV0a" +
                "F90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiNjJiMzY2ZDMtYzU2OS00OWUzLTg4MjYtODRmMTM4ODM1MDQ3IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJ" +
                "jb20uY3hyZXdhcmRzdWlhcHBzaGVsbC8iLCIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJ" +
                "yZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSw" +
                "ic2NvcGUiOiJSRVNFVF9QQVNTV09SRCBlbWFpbCBERUxFVEVfVVNFUiB1bmxvY2stdXNlci1hY2NvdW50IHByb2ZpbGUgUkVUUklFVkVfVVNFUiBVUERBVEVfVVNFUk5B" +
                "TUVfUEFTU1dPUkQgQ1JFQVRFX1VTRVIiLCJtZW1iZXJfaWQiOiIzMTQxODI2ZS1kOTUyLTQyODktYTk0OS04MmJlZTMxM2U1YWYiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2" +
                "UsIm5hbWUiOiJURVNUIFVTRVIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsaW5oIiwiZ2l2ZW5fbmFtZSI6IlRFU1QiLCJmYW1pbHlfbmFtZSI6IlVTRVIiLCJlbWFpbCI6InRvbnkuc3RhcmtAYXZlbmdlcnMub3JnIn0." +
                "DmjPjFSHAxwjqCtrAFMb_KjfIsr02ymIQJCC-BOwhyE63McdU36YBMg3D3MXqNh12mvJkC5v08hWEyxraRnpdgGYe3nqL0MIWNxhngNF7vS6pw9-x3Osci" +
                "PYW6VCOggIGDOw2y7ks3s9rgPogalS8OWFYE9e74hnYrPB-7ScMe7NraAGwDXM7lzTwJtxuEFg2TbVOTwlr1cVhNwk5nPK5k3qC4B1zSwjGzo805cb3GQ-lekPZJw6j6KGAab" +
                "RPEIcPc58mXX-d2hEFRcLYjX55t4ui0t5Rjjs3E_9j7Ltpfc_oKw-h44oddGnDb2gFUWIZCn_fKf2ERETkbFVrNnqyg";
        var wsId = UUID.randomUUID();
        var docId = UUID.randomUUID().toString();
        var workspace = new Workspace();
        workspace.setId(wsId);
        var casee = new Case();
        casee.setId(UUID.randomUUID());
        workspace.setCasee(casee);
//        workspace.setDocuments(docId);
        workspace.setBelongToCustomer(true);
        workspace.setLastAccess(LocalDateTime.now(ZoneOffset.UTC));
        workspace.getCasee().setLastUpdated(LocalDateTime.now());
        when(workspaceRepository.getById(wsId)).thenReturn(workspace);
        when(workspaceRepository.save(any())).thenReturn(workspace);
        var documentDto = new DocumentDto();
        documentDto.setId(UUID.fromString(docId));
        when(webClient.getDocumentList(any())).thenReturn(Arrays.asList(documentDto));
        var wsDocEntity = new WorkspaceDocument();
        wsDocEntity.setId(new WorkspaceDocumentPK(wsId, UUID.fromString(docId)));
        when(workspaceDocumentRepository.findAllByWorkspaceId(wsId)).thenReturn(Arrays.asList(wsDocEntity));

        var req = new WorkspaceDocumentRequest();
        req.setId(wsId.toString());
        req.setDocumentIds(Arrays.asList(docId));
        req.setCustomerDocument(true);
        var res = workspaceService.addDocuments(req, token);
        assertNotNull(res);
        assertEquals(wsId, ((WorkspaceDto)res.getData()).getId());
        ArgumentCaptor<UserCreateNotificationRequest> captor = ArgumentCaptor.forClass(UserCreateNotificationRequest.class);
        verify(webClient).createNotification(captor.capture());
        assertEquals("SHARE", captor.getValue().getActionType());
        assertEquals(wsId.toString(), captor.getValue().getNotifyIds().get(0).toString());
    }

    @Test
    void removeDocuments_happyCase() {
        var wsId = UUID.randomUUID();
        var docId = UUID.randomUUID().toString();
        var workspace = new Workspace();
        workspace.setId(wsId);
//        workspace.setDocuments(docId);
        when(workspaceRepository.getById(wsId)).thenReturn(workspace);
//        var ws2 = new Workspace();
//        ws2.setId(wsId);
//        when(workspaceRepository.save(any())).thenReturn(ws2);
        var wsDocEntity = new WorkspaceDocument();
        wsDocEntity.setId(new WorkspaceDocumentPK(wsId, UUID.fromString(docId)));
        when(workspaceDocumentRepository.findAllByWorkspaceId(wsId)).thenReturn(Arrays.asList(wsDocEntity));

        var req = new WorkspaceDocumentRequest();
        req.setId(wsId.toString());
        req.setDocumentIds(Arrays.asList(docId));
        var res = workspaceService.removeDocuments(req);
        assertNotNull(res);
        assertEquals(wsId, ((WorkspaceDto)res.getData()).getId());
        ArgumentCaptor<UUID> wsCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UUID> docCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(workspaceDocumentRepository).deleteByWorkspaceIdAndDocumentId(wsCaptor.capture(), docCaptor.capture());
        assertEquals(wsId, wsCaptor.getValue());
        assertEquals(UUID.fromString(docId), docCaptor.getValue());
    }

    @Test
    void generateToken_happyCase() {
        var wsId = UUID.randomUUID();
        var workspace = new Workspace();
        workspace.setId(wsId);
        workspace.setBelongToCustomer(Boolean.FALSE);
        workspace.setThirdPartyId(UUID.randomUUID());
        when(workspaceRepository.getById(wsId)).thenReturn(workspace);

        var res = workspaceService.generateToken(wsId.toString());
        assertNotNull(res);
        assertEquals(6, ((String)res.getData()).length());
    }

    @Test
    void isValidCustomerToken_happyCase() {
        var wsId = "3169f4cc-ab51-4a39-85d7-9919162648a8";
        var tokenHash = "9cf93fde59b3c3ff210c1c5079c55349fb76ea84824c3c82f4343432f95021fc"; //123456
        var ws = new Workspace();
        ws.setId(UUID.fromString(wsId));
        var openCase = new Case();
        openCase.setStatus(CaseStatus.OPEN);
        ws.setCasee(openCase);
        ws.setBelongToCustomer(true);
        when(workspaceRepository.findByIdAndCustomerTokenHash(UUID.fromString(wsId), tokenHash)).thenReturn(Optional.of(ws));

        var res = workspaceService.isValidCustomerToken(wsId, "123456");
        assertEquals("CUSTOMER", res);
    }

    @Test
    void isAuthorizedSignicatUser_happyCase() {
        var wsId = "3169f4cc-ab51-4a39-85d7-9919162648a8";
        var nationalId = "123456";
        var ws = new Workspace();
        ws.setId(UUID.fromString(wsId));
        var openCase = new Case();
        openCase.setStatus(CaseStatus.OPEN);
        ws.setCasee(openCase);
        when(workspaceRepository.findByIdAndCaseeCustomerNationalId(UUID.fromString(wsId), nationalId)).thenReturn(Optional.of(ws));

        var res = workspaceService.isAuthorizedSignicatUser(wsId, nationalId);
        assertTrue(res);
    }

    @Test
    void getThirdPartyUserWorkspaces_happyCase() {
        var token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIzVkR3M3N2eENPdUVENHlMRHNtLXloMEp5aHAyakg1eEJVajFnSHZiZldFIn0." +
                "eyJqdGkiOiJmMGU1MDQ5My0wNGVmLTRhYmQtODQyZS05ZWY5NTdmOWZhMTYiLCJleHAiOjE2NDIzNTg0NzAsIm5iZiI6MCwiaWF0IjoxNjQyMzIyNDcwLC" +
                "Jpc3MiOiJodHRwczovL3BsYXRmb3JtLWtleWNsb2FrLmludC5kZXYuYWZmaW5pb25zZXJ2aWNlcy5jb20vYXV0aC9yZWFsbXMvc2VjdXJlLXVwbG9hZCIs" +
                "ImF1ZCI6ImFjY291bnQiLCJzdWIiOiIwM2RmNGNkMy1kMDhlLTRiM2UtYTdhMi05ZmFlZGYzZTg0OGEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJhZG1pbi1z" +
                "ZXJ2aWNlIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiZmFhYTQ4ZTEtNmM4OC00N2FhLWIzYTUtODQ4YzIwMTQwNjQzIiwiYWNyIjoiMSIsImFsbG" +
                "93ZWQtb3JpZ2lucyI6WyJjb20uY3hyZXdhcmRzdWlhcHBzaGVsbC8iLCIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVt" +
                "YV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LW" +
                "xpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJSRVNFVF9QQVNTV09SRCBlbWFpbCBERUxFVEVfVVNFUiB1bmxvY2stdXNlci1hY2NvdW50IHByb2ZpbG" +
                "UgUkVUUklFVkVfVVNFUiBVUERBVEVfVVNFUk5BTUVfUEFTU1dPUkQgQ1JFQVRFX1VTRVIiLCJtZW1iZXJfaWQiOiJkMWVlOTlkMy1mMWExLTRhNGYtYmQ5OS1iNTV" +
                "jNDMxODlmZjgiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJsaW5oIHRoaXJkcGFydHkyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoibGluaHRwMiIsImdpdm" +
                "VuX25hbWUiOiJsaW5oIiwiZmFtaWx5X25hbWUiOiJ0aGlyZHBhcnR5MiIsImVtYWlsIjoibGluaC50aGlyZHBhcnR5MkBhdmVuZ2Vycy5vcmcifQ." +
                "n3JdLMuU-qSmBnTxUYr3-Ck49siOcuJ3maPB_NVgTNdyywBm3-mUpMW-VGdPqKLi5hQ77R-NcwE7M7OUU2Pt6tk8UvLEZYK9m9UZeySO1Rl7EuVi_ee5NbbOkE7TMScr_" +
                "VFg_EAugVkI561hOnY5kEmUgewEzlO4bI-UlFSs6hVMBRlSH6b1Wu5UqBofDWW5ZT8cJ3sQs9qqUvCvURizqfFFu3CMYFkMQgHd17gZmwsoPs3TfNZ4qWQASIfBIVvInKOFXB" +
                "g3k6cbYcsqWk5Ox56si6aGQ19--tF4W6JqZHVrskWoXlHpX47ya2jCBssBR1r7Hd4xiHwwSagUHYtTnA";
        var thirdPartyId = "d1ee99d3-f1a1-4a4f-bd99-b55c43189ff8";
        var orgId = UUID.randomUUID();
        var tpUser = new UserDto();
        var org = new OrganizationDto();
        org.setId(orgId);
        tpUser.setOrganisation(org);
        when(webClient.getThirdPartyUserById(thirdPartyId)).thenReturn(tpUser);
        var ws = new Workspace();
        ws.setId(UUID.randomUUID());
        ws.setThirdPartyId(UUID.fromString(thirdPartyId));
        var openCase = new Case();
        openCase.setStatus(CaseStatus.OPEN);
        ws.setCasee(openCase);
        when(workspaceRepository.findAllByThirdPartyId(orgId)).thenReturn(new ArrayList<>(Arrays.asList(ws)));

        var res = workspaceService.getThirdPartyUserWorkspaces(token);
        assertNotNull(res.getData());
        assertEquals(ws.getId(), ((WorkspaceDto)res.getData().get(0)).getId());
    }
}
