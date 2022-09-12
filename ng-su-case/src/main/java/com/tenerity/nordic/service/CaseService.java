package com.tenerity.nordic.service;

import com.tenerity.nordic.client.ClientDto;
import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.UserDto;
import com.tenerity.nordic.client.dto.UserCreateNotificationRequest;
import com.tenerity.nordic.dto.CaseDto;
import com.tenerity.nordic.dto.CaseManagementRequest;
import com.tenerity.nordic.dto.CaseManagementResponse;
import com.tenerity.nordic.dto.CaseSearchDto;
import com.tenerity.nordic.dto.CaseSearchRequest;
import com.tenerity.nordic.dto.CaseSearchResponse;
import com.tenerity.nordic.dto.CasesStatisticResponse;
import com.tenerity.nordic.dto.DashboardSearchParameter;
import com.tenerity.nordic.dto.SortColumn;
import com.tenerity.nordic.entity.Case;
import com.tenerity.nordic.enums.CaseDashboardSortColumn;
import com.tenerity.nordic.enums.CaseStatistic;
import com.tenerity.nordic.enums.CaseStatus;
import com.tenerity.nordic.repository.CaseRepository;
import com.tenerity.nordic.util.CaseServiceUtils;
import com.tenerity.nordic.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CaseService {
    @Autowired
    private CaseRepository caseRepository;
    @Autowired
    private DashboardRepository dashboardRepository;
    @Autowired
    private InternalWebClient webClient;
    @Autowired
    private AuditService auditService;

    static Set<DayOfWeek> excludeWeekendDays = new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));

    public CaseManagementResponse createCase(CaseManagementRequest request) {
        CaseManagementResponse response = validateCreationRequest(request);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        Case entity = new Case();
        entity.setSuperOfficeID(request.getSuperOfficeId());
        entity.setCustomerEmail(request.getCustomerEmail());
        entity.setCustomerNationalId(request.getCustomerNationalId());
        entity.setAssignedAgent(UUID.fromString(request.getAgentId()));
        entity.setClientId(UUID.fromString(request.getClientId()));

        entity.setStatus(CaseStatus.OPEN);

        Case createdCase = caseRepository.save(entity);
        response.setData(CaseServiceUtils.convertCase(createdCase));
        return response;
    }
    
    public CaseManagementResponse updateCase(String id, CaseManagementRequest request) {
        CaseManagementResponse response = validateUpdateCase(id, request);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        try {
            Case entity = caseRepository.getById(UUID.fromString(id));
            if (request.getSuperOfficeId() != null) {
                entity.setSuperOfficeID(request.getSuperOfficeId());
            }
            if (request.getCustomerEmail() != null) {
                entity.setCustomerEmail(request.getCustomerEmail());
            }
            if (request.getCustomerNationalId() != null) {
                entity.setCustomerNationalId(request.getCustomerNationalId());
            }
            if (request.getAgentId() != null) {
                var currentAgent = webClient.getAgentById(entity.getAssignedAgent());
                if (currentAgent != null && currentAgent.getIsOutOfOffice()) {
                    webClient.createNotification(createOOONotificationRequest(request.getAgentId(), entity, currentAgent));
                }
                entity.setAssignedAgent(UUID.fromString(request.getAgentId()));
            }

            Case updatedCase = caseRepository.save(entity);
            CaseDto dto = convertCaseDetail(updatedCase);
            response.setData(dto);
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setMessage(String.format("Cannot find case by given id=%s", id));
            return response;
        }
    }

    public CaseManagementResponse setDocumentActionForCase(String id, CaseManagementRequest request) {
        CaseManagementResponse response = CaseServiceUtils.validateUUID(id);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        try {
            Case entity = caseRepository.getById(UUID.fromString(id));
            entity.setLastUpdated(request.getLastUpdate());
            if (request.getDocumentsExpunged() != null) {
                entity.setDocumentsExpunged(request.getDocumentsExpunged());
            }
            if (request.getNeedAgentNotification() != null) {
                entity.setNeedAgentNotification(request.getNeedAgentNotification());
            }
            entity = caseRepository.save(entity);
            CaseDto dto = convertCaseDetail(entity);
            response.setData(dto);
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setMessage(String.format("Cannot find case by given id=%s", id));
            return response;
        }
    }

    public CaseManagementResponse closeCase(String id) {
        CaseManagementResponse response = CaseServiceUtils.validateUUID(id);
        if (response.getHttpCode() != null) {
            return response;
        }
        try {
            Case entity = caseRepository.getById(UUID.fromString(id));
            entity.setStatus(CaseStatus.CLOSE);
            caseRepository.save(entity);
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setMessage(String.format("Cannot find case by given id=%s", id));
            return response;
        }
    }

    public CaseManagementResponse deleteCaseByClient(String clientId) {
        CaseManagementResponse response = CaseServiceUtils.validateUUID(clientId);
        if (response.getHttpCode() != null) {
            return response;
        }
        try {
            List<Case> caseList = caseRepository.findAllByClientId(UUID.fromString(clientId));
            caseList.forEach(entity -> {
                webClient.deleteDocumentByCaseId(entity.getId().toString());
            });
            caseRepository.deleteAll(caseList);
            return response;
        } catch (Exception e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_DELETE_CASE_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    public CaseSearchResponse searchCase(CaseSearchRequest searchRequest) {
        CaseSearchResponse response = validateSearchRequest(searchRequest);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        List<UserDto> allAgents = webClient.getAllAgents();
        List<ClientDto> allClients = webClient.getAllClients();

        DashboardSearchParameter parameter = new DashboardSearchParameter();
        parameter.setKeyword(searchRequest.getKeyword());
        if (StringUtils.isNotBlank(searchRequest.getKeyword())) {
            List<UUID> filteredAgents = allAgents.stream().filter(user -> StringUtils.containsIgnoreCase(user.getEmailAddress(), searchRequest.getKeyword()))
                    .map(matchedUser -> matchedUser.getId()).collect(Collectors.toList());
            parameter.setAgentFilteredIds(filteredAgents);

            List<UUID> filteredClients = allClients.stream().filter(client -> StringUtils.containsIgnoreCase(client.getName(), searchRequest.getKeyword()))
                    .map(matchedClient -> matchedClient.getId()).collect(Collectors.toList());
            parameter.setClientFilteredIds(filteredClients);
        }

        if(StringUtils.isNotBlank(searchRequest.getAgentId())) {
            parameter.setAgentId(UUID.fromString(searchRequest.getAgentId()));
        }
        if (StringUtils.isNotBlank(searchRequest.getThirdPartyId())) {
            parameter.setThirdPartyId(UUID.fromString(searchRequest.getThirdPartyId()));
        }

        CaseStatus status = getStatusByCaseStatistic(searchRequest.getCaseStatistic());
        parameter.setStatus(status);
        parameter.setCaseStatistic(searchRequest.getCaseStatistic());
        parameter.setTimeFrom(searchRequest.getDateFrom());
        parameter.setTimeTo(searchRequest.getDateTo());

        List<Case> allEntities = dashboardRepository.searchCases(parameter);
        if (!allEntities.isEmpty()) {
            List<CaseSearchDto> searchDtos = allEntities.stream().map(item -> convertObject(item, allAgents, allClients)).collect(Collectors.toList());
            if (searchRequest.getSortColumn() != null) {
                searchDtos = sortDatalist(searchDtos, searchRequest.getSortColumn());
            } else {
                searchDtos = sortFirstTimeLoad(searchDtos);
            }

            applyPaginationToResponse(response, searchDtos, searchRequest.getSize(), searchRequest.getPage());
        }

        return response;
    }

    private boolean isFirstTimeLoad(CaseSearchRequest searchRequest) {
        return StringUtils.isBlank(searchRequest.getKeyword()) &&
                StringUtils.isBlank(searchRequest.getAgentId()) &&
                StringUtils.isBlank(searchRequest.getThirdPartyId()) &&
                CaseStatistic.ALL == searchRequest.getCaseStatistic() &&
                searchRequest.getDateFrom() == null && searchRequest.getDateTo() == null &&
                searchRequest.getSortColumn() == null;
    }

    private void applyPaginationToResponse(CaseSearchResponse response, List<CaseSearchDto> dtos, Integer size, Integer page) {
        response.setTotalItem(dtos.size());
        int totalPages = (int) Math.ceil((double)response.getTotalItem() / size);
        response.setTotalPage(totalPages);
        int firstResult = 0;
        if (page >= 0 && page < totalPages) {
            firstResult = page * size;
        }
        int count = Math.min(size, response.getTotalItem() - firstResult);
        response.getResults().addAll(dtos.subList(firstResult, firstResult + count));
    }

    private List<CaseSearchDto> sortDatalist(List<CaseSearchDto> searchDtos, SortColumn sortColumn) {
        Stream<CaseSearchDto> stream = searchDtos.stream();
        switch (CaseDashboardSortColumn.valueOf(sortColumn.getColumnName())) {
            case SUPER_OFFICE_ID:
                if (sortColumn.getDescending()) {
                    stream = stream.sorted(Comparator.comparing(CaseSearchDto::getSuperOfficeID, Comparator.nullsLast(Comparator.reverseOrder())));
                } else {
                    stream = stream.sorted(Comparator.comparing(CaseSearchDto::getSuperOfficeID, Comparator.nullsLast(Comparator.naturalOrder())));
                }
                break;
            case ASSIGNED_AGENT:
                if (sortColumn.getDescending()) {
                    stream = stream.sorted(Comparator.comparing(CaseSearchDto::getAssignedAgent, Comparator.nullsLast(Comparator.reverseOrder())));
                } else {
                    stream = stream.sorted(Comparator.comparing(CaseSearchDto::getAssignedAgent, Comparator.nullsLast(Comparator.naturalOrder())));
                }
                break;
            case CLIENT:
                if (sortColumn.getDescending()) {
                    stream = stream.sorted(Comparator.comparing(CaseSearchDto::getClient, Comparator.nullsLast(Comparator.reverseOrder())));
                } else {
                    stream = stream.sorted(Comparator.comparing(CaseSearchDto::getClient, Comparator.nullsLast(Comparator.naturalOrder())));
                }
                break;
            case CUSTOMER_EMAIL:
                if (sortColumn.getDescending()) {
                    stream = stream.sorted(Comparator.comparing(CaseSearchDto::getCustomerEmail, Comparator.nullsLast(Comparator.reverseOrder())));
                } else {
                    stream = stream.sorted(Comparator.comparing(CaseSearchDto::getCustomerEmail, Comparator.nullsLast(Comparator.naturalOrder())));
                }
                break;
            case LAST_UPDATED:
                if (sortColumn.getDescending()) {
                    stream = stream.sorted(Comparator.comparing(CaseSearchDto::getLastUpdated, Comparator.nullsLast(Comparator.reverseOrder())));
                } else {
                    stream = stream.sorted(Comparator.comparing(CaseSearchDto::getLastUpdated, Comparator.nullsLast(Comparator.naturalOrder())));
                }
                break;
            default:
                stream = stream.sorted(Comparator.comparing(CaseSearchDto::getImportancePoint, Comparator.reverseOrder())
                                        .thenComparing(CaseSearchDto::getLastUpdated, Comparator.nullsLast(Comparator.reverseOrder())));
                break;
        }
        return stream.collect(Collectors.toList());
    }

    private List<CaseSearchDto> sortFirstTimeLoad(List<CaseSearchDto> searchDtos) {
        Collections.sort(searchDtos, Comparator.comparing(CaseSearchDto::getImportancePoint, Comparator.reverseOrder())
                                                .thenComparing(CaseSearchDto::getLastUpdated, Comparator.nullsLast(Comparator.reverseOrder())));
        return searchDtos;
    }


    private CaseSearchResponse validateSearchRequest(CaseSearchRequest searchRequest) {
        StringBuilder msg = new StringBuilder();
        if (searchRequest.getCaseStatistic() == null) {
            msg.append("caseStatistic is required. ");
        }
        if (searchRequest.getPage() == null) {
            msg.append("page is required. ");
        }
        if (searchRequest.getSize() == null) {
            msg.append("size is required. ");
        }

        CaseSearchResponse response = new CaseSearchResponse();
        if (msg.length() > 0) {
            response.setMessage(msg.toString());
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    private CaseStatus getStatusByCaseStatistic(CaseStatistic caseStatistic) {
        if (CaseStatistic.ALL == caseStatistic) {
            return null;
        } else if (CaseStatistic.RESOLVED_CASE == caseStatistic) {
            return CaseStatus.CLOSE;
        }
        return CaseStatus.OPEN;
    }

    public CasesStatisticResponse getCasesStatistics() {
        CasesStatisticResponse response = new CasesStatisticResponse();
        ArrayList<Case> allCases = (ArrayList<Case>) caseRepository.findAll();
        response.setAllCases(Long.valueOf(allCases.size()));

        response.setNewCases(filterBasedOnStatistic(allCases.stream(), CaseStatistic.NEW_CASE).count());
        response.setOpenCasesExceed24Hours(filterBasedOnStatistic(allCases.stream(), CaseStatistic.OPEN_CASE_EXCEED_24H).count());
        response.setOpenCasesExceed72Hours(filterBasedOnStatistic(allCases.stream(), CaseStatistic.OPEN_CASE_EXCEED_72H).count());
        response.setResolvedCases(filterBasedOnStatistic(allCases.stream(), CaseStatistic.RESOLVED_CASE).count());
        return response;
    }

    private Stream<Case> filterBasedOnStatistic(Stream<Case> stream, CaseStatistic caseStatistic) {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        LocalDateTime seventyTwoHoursAgo = LocalDateTime.now().minusHours(72);
        if (excludeWeekendDays.contains(LocalDateTime.now().getDayOfWeek())) {
            seventyTwoHoursAgo = seventyTwoHoursAgo.minusHours(24);
        }
        switch (caseStatistic) {
            case NEW_CASE:
                return stream.filter(item -> CaseStatus.OPEN == item.getStatus() && (item.getNeedAgentNotification() == null || !item.getNeedAgentNotification()));
            case OPEN_CASE_EXCEED_24H:
                LocalDateTime finalSeventyTwoHoursAgo1 = seventyTwoHoursAgo;
                return stream.filter(item -> CaseStatus.OPEN == item.getStatus() && item.getLastUpdated() != null && item.getLastUpdated().isAfter(finalSeventyTwoHoursAgo1)
                        && item.getNeedAgentNotification() != null && item.getNeedAgentNotification());
            case OPEN_CASE_EXCEED_72H:
                LocalDateTime finalSeventyTwoHoursAgo = seventyTwoHoursAgo;
                return stream.filter(item -> CaseStatus.OPEN == item.getStatus()  && item.getLastUpdated() != null
                        && item.getLastUpdated().isBefore(finalSeventyTwoHoursAgo)
                        && item.getNeedAgentNotification() != null && item.getNeedAgentNotification());
            case RESOLVED_CASE:
                return stream.filter(item -> CaseStatus.CLOSE == item.getStatus());
            case ALL:
            default:
                return stream;
        }
    }

    private static String convertToResponseStatus(Case item) {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        LocalDateTime seventyTwoHoursAgo = LocalDateTime.now().minusHours(72);
        if (excludeWeekendDays.contains(LocalDateTime.now().getDayOfWeek())) {
            seventyTwoHoursAgo = seventyTwoHoursAgo.minusHours(24);
        }

        if (CaseStatus.CLOSE == item.getStatus()) {
            return CaseStatistic.RESOLVED_CASE.name();
        } else if (item.getLastUpdated() != null && item.getLastUpdated().isBefore(seventyTwoHoursAgo)
                && item.getNeedAgentNotification() != null && item.getNeedAgentNotification()) {
            return CaseStatistic.OPEN_CASE_EXCEED_72H.name();
        }
        else if (item.getLastUpdated() != null
                && item.getLastUpdated().compareTo(seventyTwoHoursAgo) >= 0
                && item.getNeedAgentNotification() != null && item.getNeedAgentNotification()) {
            return CaseStatistic.OPEN_CASE_EXCEED_24H.name();
        } else {
            return CaseStatistic.NEW_CASE.name();
        }
    }

    private CaseSearchDto convertObject(Case entity, List<UserDto> allAgents, List<ClientDto> allClients) {
        CaseSearchDto dto = new CaseSearchDto();
        dto.setId(entity.getId());
        dto.setSuperOfficeID(entity.getSuperOfficeID());
        if (entity.getAssignedAgent() != null) {
            var optionalAgent = allAgents.stream().filter(agent -> agent.getId().compareTo(entity.getAssignedAgent()) == 0).findFirst();
            if (optionalAgent.isPresent()) {
                dto.setAssignedAgent(optionalAgent.get().getEmailAddress());
                dto.setAgentOoo(optionalAgent.get().getIsOutOfOffice());
            }
        }
        if (entity.getClientId() != null) {
            var optionalClient = allClients.stream().filter(client -> client.getId().compareTo(entity.getClientId()) == 0).findFirst();
            if (optionalClient.isPresent()) {
                dto.setClient(optionalClient.get().getName());
            }
        }
        dto.setCustomerEmail(entity.getCustomerEmail());
        dto.setLastUpdated(entity.getLastUpdated());
        dto.setStatus(convertToResponseStatus(entity));

        //calculate importance points
        int point = CaseStatus.OPEN == entity.getStatus() ? 10 : 0;
        point += dto.getAssignedAgent() == null ? 5 : 0;
        switch (dto.getStatus()) {
            case "OPEN_CASE_EXCEED_72H":
                point +=2;
                break;
            case "OPEN_CASE_EXCEED_24H":
                point +=1;
                break;
        }

        dto.setImportancePoint(point);
        return dto;
    }

    private CaseManagementResponse validateCreationRequest(CaseManagementRequest request) {
        List<String> requiredFields = new ArrayList<>();
        if (StringUtils.isBlank(request.getSuperOfficeId())) {
            requiredFields.add("superOfficeId");
        }
//        if (StringUtils.isBlank(request.getCustomerEmail())) {
//            requiredFields.add("customerEmail");
//        }
//        if (StringUtils.isBlank(request.getCustomerNationalId())) {
//            requiredFields.add("customerNationalId");
//        }
        if (StringUtils.isBlank(request.getAgentId())) {
            requiredFields.add("agentId");
        }
        if (StringUtils.isBlank(request.getClientId())) {
            requiredFields.add("clientId");
        }

        String message = null;
        if (!requiredFields.isEmpty()) {
            message = String.join(",", requiredFields) + " required";
        }
        if (message == null) {
            var entity = caseRepository.findBySuperOfficeIDIgnoreCase(request.getSuperOfficeId());
            if (entity.isPresent()) {
                message = "SuperOfficeId " + request.getSuperOfficeId() + " is exists!";
            }
        }

        CaseManagementResponse response = new CaseManagementResponse();
        response.setMessage(message);
        response.setHttpCode(message != null ? HttpStatus.BAD_REQUEST.value() : null);
        return response;
    }

    private CaseManagementResponse validateUpdateCase(String id, CaseManagementRequest request) {
        CaseManagementResponse response = CaseServiceUtils.validateUUID(id);
        if (request == null) {
            response.setHttpCode(HttpStatus.NO_CONTENT.value());
            response.setMessage("No changes");
            return response;
        }
        if (StringUtils.isNotBlank(request.getSuperOfficeId())) {
            var entity = caseRepository.findBySuperOfficeIDIgnoreCase(request.getSuperOfficeId());
            if (entity.isPresent()) {
                response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage(String.format("SuperOfficeId %s already exists!", request.getSuperOfficeId()));
                return response;
            }
        }
        return response;
    }

    public CaseManagementResponse getCaseById(String id) {
        CaseManagementResponse response = CaseServiceUtils.validateUUID(id);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        try {
            Case entity = caseRepository.getById(UUID.fromString(id));
            CaseDto dto = convertCaseDetail(entity);
            response.setData(dto);
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find case by given id=%s", id));
            return response;
        }
    }

    private CaseDto convertCaseDetail(Case entity) {
        var dto = CaseServiceUtils.convertCase(entity);
        dto.setAssignedAgent(webClient.getAgentById(entity.getAssignedAgent()));
        if (dto.getWorkspaces() != null && !dto.getWorkspaces().isEmpty()) {
            dto.getWorkspaces().stream().forEach(item -> {
                if (item.getThirdPartyId() != null) {
                    var organization = webClient.getOrganizationById(item.getThirdPartyId());
                    if (organization != null) {
                        item.setThirdPartyName(organization.getName());
                    }
                }
                var auditDto = auditService.getLatestAudit(item.getId().toString());
                if (auditDto != null) {
                    item.setAccessTime(auditDto.getLastUpdated());
                    item.setRemoteAddress(auditDto.getRemoteAddr());
                }
            });
        }
        var client = webClient.getClientById(entity.getClientId());
        if (client != null) {
            dto.setClient(client);
        }
        return dto;
    }

    private UserCreateNotificationRequest createOOONotificationRequest(String agentId, Case entity, UserDto currentAgent) {
        var request = new UserCreateNotificationRequest();
        request.setNotifyIds(Arrays.asList(UUID.fromString(agentId)));
        request.setCaseId(entity.getId().toString());
        request.setCaseName(entity.getSuperOfficeID());
        request.setActionType("ASSIGN");
        request.setActionObject(entity.getSuperOfficeID());
        request.setActionAuthorId(currentAgent.getId().toString());
        return request;
    }
}
