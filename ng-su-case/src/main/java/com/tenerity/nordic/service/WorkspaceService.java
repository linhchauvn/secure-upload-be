package com.tenerity.nordic.service;

import com.tenerity.nordic.client.ClientDto;
import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.UserDto;
import com.tenerity.nordic.client.dto.UserCreateNotificationRequest;
import com.tenerity.nordic.client.dto.WorkspaceDeletionRequest;
import com.tenerity.nordic.dto.AuditCreationRequest;
import com.tenerity.nordic.dto.CaseManagementResponse;
import com.tenerity.nordic.dto.WorkspaceDataListResponse;
import com.tenerity.nordic.dto.WorkspaceDocumentRequest;
import com.tenerity.nordic.dto.WorkspaceDto;
import com.tenerity.nordic.dto.WorkspaceManagementRequest;
import com.tenerity.nordic.entity.Workspace;
import com.tenerity.nordic.entity.WorkspaceDocument;
import com.tenerity.nordic.entity.WorkspaceDocumentPK;
import com.tenerity.nordic.enums.CaseStatus;
import com.tenerity.nordic.enums.WorkspaceType;
import com.tenerity.nordic.repository.CaseRepository;
import com.tenerity.nordic.repository.WorkspaceDocumentRepository;
import com.tenerity.nordic.repository.WorkspaceRepository;
import com.tenerity.nordic.util.CaseServiceUtils;
import com.tenerity.nordic.util.ConfigProperties;
import com.tenerity.nordic.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {
    @Autowired
    private CaseRepository caseRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private WorkspaceDocumentRepository workspaceDocumentRepository;
    @Autowired
    private InternalWebClient webClient;
    @Autowired
    private AuditService auditService;
    @Autowired
    private ConfigProperties configProperties;

    @Value("${basicAuth.username}")
    private String username;
    @Value("${basicAuth.password}")
    private String password;


    private static final String hashKey = "vONAvShYvt";


    public CaseManagementResponse createWorkspace(WorkspaceManagementRequest request) {
        CaseManagementResponse response = validateCreationRequest(request);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        var caseOptional = caseRepository.findById(UUID.fromString(request.getCaseId()));
        if (!caseOptional.isPresent()) {
            response.setMessage(String.format("Cannot find case by given id=%s", request.getCaseId()));
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            return response;
        }

        if (request.getType() == null && caseOptional.get().getWorkspaces() != null) {
            var workspaces = caseOptional.get().getWorkspaces();
            if (workspaces.stream().filter(item -> !item.getBelongToCustomer()).anyMatch(item -> item.getLabel().equals(request.getLabel())
                    && item.getThirdPartyId() != null && request.getThirdPartyId() != null
                    && item.getThirdPartyId().compareTo(UUID.fromString(request.getThirdPartyId())) == 0)) {
                response.setMessage(String.format("Workspace %s already exists.", request.getLabel()));
                response.setCode(Constants.ERROR_CODE_WORKSPACE_DUPLICATED_INPUT);
                response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                return response;
            }
        }

        Workspace entity = new Workspace();
        entity.setCasee(caseOptional.get());
        if (StringUtils.isBlank(request.getType())) {
            entity.setBelongToCustomer(Boolean.FALSE);
            entity.setLabel(request.getLabel());
            if (StringUtils.isNotBlank(request.getThirdPartyId())) {
                entity.setThirdPartyId(UUID.fromString(request.getThirdPartyId()));
            }
        } else {
            entity.setBelongToCustomer(Boolean.TRUE);
            entity.setBankIdLogin(WorkspaceType.BANKID.name().equals(request.getType()) ? Boolean.TRUE : Boolean.FALSE);
        }

        entity = workspaceRepository.save(entity);
        var dto = CaseServiceUtils.convertWorkspace(entity);
        if (dto.getThirdPartyId() != null) {
            var organization = webClient.getOrganizationById(dto.getThirdPartyId());
            if (organization != null) {
                dto.setThirdPartyName(organization.getName());
            }
        }
        response.setData(dto);
        return response;
    }

    private CaseManagementResponse validateCreationRequest(WorkspaceManagementRequest request) {
        CaseManagementResponse response = new CaseManagementResponse();
        if (StringUtils.isBlank(request.getCaseId())) {
            response.setMessage("caseId must not be empty!");
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
        }
        response = CaseServiceUtils.validateUUID(request.getCaseId());
        if (StringUtils.isBlank(request.getLabel()) && StringUtils.isBlank(request.getType())) {
            String message = "Either label or type must not be empty!";
            response.setMessage(message);
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    public CaseManagementResponse getWorkspaceById(String id, String authToken, String clientIp) {
        CaseManagementResponse response = CaseServiceUtils.validateUUID(id);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        var memberId = CaseServiceUtils.extractUserUUIDFromToken(authToken);
        var isAgent = false;
        if(memberId == null){
            isAgent = CaseServiceUtils.isInternal(authToken, String.format("%s:%s", username, password));
            if (!isAgent) {
                response.setHttpCode(HttpStatus.FORBIDDEN.value());
                response.setCode(Constants.WORKSPACE_UNAUTHORIZED_ACCESS);
                response.setMessage("Login user is not authorized to access this workspace.");
                return response;
            }
        } else {
            isAgent = isAgent(memberId);
            if (!id.equals(memberId) && !isAgent && !isAuthorizedTPUser(id, memberId)) {
                response.setHttpCode(HttpStatus.FORBIDDEN.value());
                response.setCode(Constants.WORKSPACE_UNAUTHORIZED_ACCESS);
                response.setMessage("Login user is not authorized to access this workspace.");
                return response;
            }
        }



        try {
            Workspace entity = workspaceRepository.getById(UUID.fromString(id));
            if (!isAgent) {
                var value = ServletUriComponentsBuilder.fromCurrentRequest().build().toString();
                AuditCreationRequest auditReq = new AuditCreationRequest();
                auditReq.setUriAccessed(value);
                auditReq.setRemoteAddr(clientIp);
                auditReq.setLocalRef(id);
                auditReq.setLatest(true);
                auditService.createAuditData(auditReq);

                entity.setLastAccess(LocalDateTime.now(ZoneOffset.UTC));
                workspaceRepository.save(entity);
            }
            var dto = convertWorkspaceDetail(entity);
            response.setData(dto);
            return response;
        } catch (EntityNotFoundException e) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find workspace by given id=%s", id));
            return response;
        }
    }

    private boolean isAuthorizedTPUser(String id, String memberId) {
        var tpUser = webClient.getThirdPartyUserById(memberId);
        if (tpUser == null || tpUser.getOrganisation() == null) {
            return false;
        }
        var optional = workspaceRepository.findById(UUID.fromString(id));
        return optional.isPresent() && tpUser.getOrganisation().getId().equals(optional.get().getThirdPartyId());
    }

    private boolean isAgent(String memberId) {
        return webClient.getAgentById(UUID.fromString(memberId)) != null;
    }

    public CaseManagementResponse deleteWorkspace(String id) {
        CaseManagementResponse response = CaseServiceUtils.validateUUID(id);
        if (response.getHttpCode() != null) {
            return response;
        }

        try {
            Workspace entity = workspaceRepository.getById(UUID.fromString(id));
            var dto = convertWorkspaceDetail(entity);
            if(dto.getDocuments() != null) {
                dto.getDocuments().forEach(item -> {
                    workspaceDocumentRepository.deleteByWorkspaceIdAndDocumentId(dto.getId(), item.getId());
                });
            }
            workspaceRepository.deleteById(UUID.fromString(id));
            return response;
        } catch (EmptyResultDataAccessException e) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find workspace by given id=%s", id));
            return response;
        }
    }

    public CaseManagementResponse requestDeleteWorkspace(String id) {
        CaseManagementResponse response = CaseServiceUtils.validateUUID(id);
        if (response.getHttpCode() != null) {
            return response;
        }

        var optional = workspaceRepository.findById(UUID.fromString(id));
        if (!optional.isPresent()) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find workspace by given id=%s", id));
            return response;
        }
        var workspace = optional.get();
        // send email
        var agent = webClient.getAgentById(workspace.getCasee().getAssignedAgent());
        var client = webClient.getClientById(workspace.getCasee().getClientId());
        if (agent != null && client != null) {
            webClient.triggerDeletionEmail(deletionRequest(workspace, agent, client));
        }
        // send  notification
        webClient.createNotification(createRequestDeleteNotificationRequest(workspace.getCasee().getAssignedAgent(), workspace));

        return response;
    }

    public CaseManagementResponse deleteWorkspaceByThirdPartyId(String id) {
        CaseManagementResponse response = CaseServiceUtils.validateUUID(id);
        if (response.getHttpCode() != null) {
            return response;
        }

        try {
            var workspaces = workspaceRepository.findAllByThirdPartyId(UUID.fromString(id));
            workspaceRepository.deleteAll(workspaces);
            return response;
        } catch (Exception e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_DELETE_WORKSPACE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    public CaseManagementResponse addDocuments(WorkspaceDocumentRequest request, String authToken) {
        CaseManagementResponse response = validateWorkspaceDocument(request);
        if (request.getDocumentIds() != null && !request.getDocumentIds().isEmpty()) {
            try {
                Workspace entity = workspaceRepository.getById(UUID.fromString(request.getId()));
//                String documentIds = String.join(",", request.getDocumentIds());
//                if (StringUtils.isNotBlank(entity.getDocuments())) {
//                    documentIds = String.join(Constants.COMMA_DELIMITER, entity.getDocuments(), documentIds);
//                }
                if (request.isCustomerDocument()) {
                    entity.setLastAccess(LocalDateTime.now(ZoneOffset.UTC));
                    entity.getCasee().setLastUpdated(LocalDateTime.now());
                }
                List<WorkspaceDocument> workspaceDocuments = new ArrayList<>();
                for (String docId : request.getDocumentIds()) {
                    WorkspaceDocument wsDoc = new WorkspaceDocument();
                    wsDoc.setId(new WorkspaceDocumentPK(entity.getId(), UUID.fromString(docId)));
                    workspaceDocuments.add(wsDoc);
                }
                var res = workspaceDocumentRepository.saveAll(workspaceDocuments);
//                entity.setDocuments(documentIds);
                entity = workspaceRepository.save(entity);
                var dto = CaseServiceUtils.convertWorkspace(entity);
                var wsDoc = workspaceDocumentRepository.findAllByWorkspaceId(entity.getId());
                if (wsDoc != null && !wsDoc.isEmpty()) {
                    var documentIds = String.join(Constants.COMMA_DELIMITER, wsDoc.stream().map(item -> item.getId().getDocumentId().toString()).collect(Collectors.toList()));
                    var documentDtos = webClient.getDocumentList(documentIds);
                    dto.setDocumentIds(documentIds);
                    dto.setDocuments(documentDtos);

                    var selectedDocument = documentDtos.stream().filter(item -> request.getDocumentIds().contains(item.getId().toString())).collect(Collectors.toList());
                    var agentId = CaseServiceUtils.extractUserUUIDFromToken(authToken);
                    if (!selectedDocument.isEmpty() && StringUtils.isNotBlank(agentId)) {
                        selectedDocument.forEach(doc -> {
                            webClient.createNotification(createShareNotificationRequest(agentId, doc.getLabel(), dto));
                        });
                    }
                }
                response.setData(dto);
                return response;
            } catch (EntityNotFoundException ee) {
                response.setHttpCode(HttpStatus.NOT_FOUND.value());
                response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
                response.setMessage(String.format("Cannot find workspace by given id=%s", request.getId()));
                return response;
            }
        }
        return response;
    }

    public CaseManagementResponse removeDocuments(WorkspaceDocumentRequest request) {
        CaseManagementResponse response = validateWorkspaceDocument(request);
        if (request.getDocumentIds() != null && !request.getDocumentIds().isEmpty()) {
            try {
                Workspace entity = workspaceRepository.getById(UUID.fromString(request.getId()));
//                if (StringUtils.isNotBlank(entity.getDocuments())) {
//                    List<String> ids = new LinkedList<>(Arrays.asList(entity.getDocuments().split(Constants.COMMA_DELIMITER)));
//                    ids.removeAll(request.getDocumentIds());
//                    entity.setDocuments(String.join(Constants.COMMA_DELIMITER, ids));
//                }
                final var workspaceId = entity.getId();
                request.getDocumentIds().forEach(item -> {
                    workspaceDocumentRepository.deleteByWorkspaceIdAndDocumentId(workspaceId, UUID.fromString(item));
                });
                var dto = CaseServiceUtils.convertWorkspace(entity);
                var wsDoc = workspaceDocumentRepository.findAllByWorkspaceId(entity.getId());
                if (wsDoc != null && !wsDoc.isEmpty()) {
                    var documentIds = String.join(Constants.COMMA_DELIMITER, wsDoc.stream().map(item -> item.getId().getDocumentId().toString()).collect(Collectors.toList()));
                    var documentDtos = webClient.getDocumentList(documentIds);
                    dto.setDocumentIds(documentIds);
                    dto.setDocuments(documentDtos);
                }
                response.setData(dto);
                return response;
            } catch (EntityNotFoundException ee) {
                response.setHttpCode(HttpStatus.NOT_FOUND.value());
                response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
                response.setMessage(String.format("Cannot find workspace by given id=%s", request.getId()));
                return response;
            }
        }
        return response;
    }

    public CaseManagementResponse generateToken(String id) {
        CaseManagementResponse response = new CaseManagementResponse();
        try {
            Workspace entity = workspaceRepository.getById(UUID.fromString(id));
            if ((!entity.getBelongToCustomer() && entity.getThirdPartyId() != null) ||
                    (entity.getBelongToCustomer() && entity.getBankIdLogin())) {
                response.setHttpCode(HttpStatus.FORBIDDEN.value());
                response.setCode(Constants.WORKSPACE_UNAUTHORIZED_ACCESS);
                response.setMessage(String.format("Workspace %s is not anonymous access type.", id));
            }
            Random rnd = new Random();
            int number = rnd.nextInt(999999);
            String formattedNumber =  String.format("%06d", number);

            entity.setCustomerTokenHash(hashCustomerToken(id, formattedNumber));
            workspaceRepository.save(entity);
            response.setData(formattedNumber);
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find workspace by given id=%s", id));
            return response;
        }
    }

    public String isValidCustomerToken(String id, String token) {
        try {
            String tokenHash = hashCustomerToken(id, token);
            var entity = workspaceRepository.findByIdAndCustomerTokenHash(UUID.fromString(id), tokenHash);
            if (entity.isPresent() && isBelongToOpenCase(entity.get())) {
                if (entity.get().getBelongToCustomer()) {
                    return "CUSTOMER";
                } else {
                    return "THIRDPARTY_OTP";
                }
            }
        } catch (NullPointerException | IllegalArgumentException e) {

        }
        return "";
    }

    public Boolean isAuthorizedSignicatUser(String id, String nationalId) {
        try {
            var entity = workspaceRepository.findByIdAndCaseeCustomerNationalId(UUID.fromString(id), nationalId);
            if (entity.isPresent() && isBelongToOpenCase(entity.get())) {
                return true;
            }
        } catch (NullPointerException | IllegalArgumentException e) {

        }
        return false;
    }

    public WorkspaceDataListResponse getThirdPartyUserWorkspaces(String userToken) {
        WorkspaceDataListResponse response = new WorkspaceDataListResponse();
        response.setData(new ArrayList());

        String thirdPartyUserId = CaseServiceUtils.extractUserUUIDFromToken(userToken);
        var tpUser = webClient.getThirdPartyUserById(thirdPartyUserId);
        if (tpUser != null && tpUser.getOrganisation() != null) {
            var workspaces = workspaceRepository.findAllByThirdPartyId(tpUser.getOrganisation().getId());
            if (workspaces != null) {
                response.setData(workspaces.stream().filter(item -> isBelongToOpenCase(item))
                        .map(CaseServiceUtils::convertWorkspace).collect(Collectors.toList()));
            }
        }
        return response;
    }

    public String getClientLocale(String id) {
        var optional = workspaceRepository.findById(UUID.fromString(id));
        if (optional.isPresent()) {
            var clientId = optional.get().getCasee().getClientId();
            var clientDto = webClient.getClientById(clientId);
            if (clientDto != null) {
                return clientDto.getLocale().name();
            }
        }

        return null;
    }

    public CaseManagementResponse deleteDocumentReference(String docId) {
        CaseManagementResponse response = CaseServiceUtils.validateUUID(docId);
        try {
            workspaceDocumentRepository.deleteByDocumentId(UUID.fromString(docId));
            return response;
        } catch (Exception e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_DELETE_WORKSPACE_DOCUMENT_ERROR);
            response.setMessage(String.format("Error while deleteDocumentReference, id=%s", docId) + e.getMessage());
            return response;
        }
    }

    private CaseManagementResponse validateWorkspaceDocument(WorkspaceDocumentRequest request) {
        CaseManagementResponse response = new CaseManagementResponse();
        if (StringUtils.isBlank(request.getId())) {
            response.setMessage("id must not be empty!");
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
        }
        response = CaseServiceUtils.validateUUID(request.getId());
        return response;
    }

    private WorkspaceDto convertWorkspaceDetail(Workspace entity) {
        var dto = CaseServiceUtils.convertWorkspace(entity);
        if (entity.getCasee() != null && entity.getCasee().getClientId() != null) {
            var client = webClient.getClientById(entity.getCasee().getClientId());
            if (client != null) {
                dto.setClient(client);
            }
        }
        var wsDoc = workspaceDocumentRepository.findAllByWorkspaceId(entity.getId());
        if (wsDoc != null && !wsDoc.isEmpty()) {
            var documentIds = String.join(Constants.COMMA_DELIMITER, wsDoc.stream().map(item -> item.getId().getDocumentId().toString()).collect(Collectors.toList()));
            var documentDtos = webClient.getDocumentList(documentIds);
            dto.setDocumentIds(documentIds);
            dto.setDocuments(documentDtos);
        }
//        if (StringUtils.isNotBlank(entity.getDocuments())) {
//            var documentDtos = webClient.getDocumentList(entity.getDocuments());
//            dto.setDocuments(documentDtos);
//        }

        return dto;
    }

    private String hashCustomerToken(String workspaceId, String token) {
        String input = hashKey + workspaceId + token;
        try {
            String hashString = CaseServiceUtils.SHA256(input);
            return hashString;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private UserCreateNotificationRequest createRequestDeleteNotificationRequest(UUID agentId, Workspace entity) {
        var request = new UserCreateNotificationRequest();
        request.setNotifyIds(Arrays.asList(agentId));
        request.setCaseId(entity.getCasee().getId().toString());
        request.setCaseName(entity.getCasee().getSuperOfficeID());
        request.setActionType("DELETE");
        request.setActionObject(entity.getLabel());
        return request;
    }

    private UserCreateNotificationRequest createShareNotificationRequest(String agentId, String label, WorkspaceDto workspace) {
        var request = new UserCreateNotificationRequest();
        if (workspace.getBelongToCustomer()) {
            request.setNotifyIds(Arrays.asList(workspace.getId()));
        } else {
            request.setOrganizationId(workspace.getThirdPartyId());
        }

        request.setCaseId(workspace.getCaseId().toString());
        request.setCaseName(workspace.getSuperOfficeId());
        request.setActionType("SHARE");
        request.setActionObject(label);
        request.setActionAuthorId(agentId);
        return request;
    }

    private WorkspaceDeletionRequest deletionRequest(Workspace ws, UserDto agent, ClientDto client){
        var request  = new WorkspaceDeletionRequest();
        request.setWorkspaceLabel(ws.getLabel());
        request.setCustomerEmail(ws.getCasee().getCustomerEmail());
        request.setWorkspaceUrl(String.format(configProperties.getWorkspaceUrl(), ws.getId().toString()));
        request.setCaseUrl(String.format(configProperties.getCaseUrl(), ws.getCasee().getId().toString()));
        request.setAgentEmail(agent.getEmailAddress());
        request.setOrganizationName(client.getName());
        return request;
    }

    private boolean isBelongToOpenCase(Workspace workspace) {
        return workspace.getCasee() != null && CaseStatus.OPEN == workspace.getCasee().getStatus();
    }
}
