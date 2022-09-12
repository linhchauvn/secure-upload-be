package com.tenerity.nordic.service;

import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.dto.AdminDataResponse;
import com.tenerity.nordic.dto.AdminPanelManagementResponse;
import com.tenerity.nordic.dto.AdminPanelSearchRequest;
import com.tenerity.nordic.dto.AdminPanelSearchResponse;
import com.tenerity.nordic.dto.OrganizationDto;
import com.tenerity.nordic.dto.OrganizationManagementRequest;
import com.tenerity.nordic.entity.Organization;
import com.tenerity.nordic.enums.Locale;
import com.tenerity.nordic.repository.OrganizationRepository;
import com.tenerity.nordic.util.AdminPanelUtils;
import com.tenerity.nordic.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationManagementService {
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private InternalWebClient webClient;

    public AdminDataResponse getAllThirdParties() {
        List<Organization> entities = organizationRepository.findAll();
        List<OrganizationDto> dtos = entities.stream().map(AdminPanelUtils::convertOrganization).collect(Collectors.toList());
        AdminDataResponse response = new AdminDataResponse();
        response.setData(dtos);
        return response;
    }

    public AdminPanelSearchResponse searchOrganization(AdminPanelSearchRequest searchRequest) {
        AdminPanelSearchResponse response = AdminPanelUtils.validateSearchRequest(searchRequest);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        Pageable pageable = AdminPanelUtils.buildPagingAndSorting(searchRequest.getPage(), searchRequest.getSize(), searchRequest.getSortColumn());
        Page<Organization> entities;
        if (StringUtils.isBlank(searchRequest.getKeyword())) {
            entities = organizationRepository.findAll(pageable);
        } else {
            entities = organizationRepository.searchOrganizationFuzzyWithPagination(searchRequest.getKeyword(), pageable);
        }

        response.setTotalItem(entities.getTotalElements());
        response.setTotalPage(entities.getTotalPages());
        if (entities.getTotalElements() > 0) {
            response.getResults().addAll(entities.get().map(AdminPanelUtils::convertOrganization).collect(Collectors.toList()));
        }

        return response;
    }

    public AdminPanelManagementResponse findOrganizationById(String id) {
        AdminPanelManagementResponse response = AdminPanelUtils.validateUUID(id);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        try {
            Organization entity = organizationRepository.getById(UUID.fromString(id));
            response.setData(AdminPanelUtils.convertOrganization(entity));
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find client by given id=%s", id));
            return response;
        }
    }

    public AdminPanelManagementResponse createOrganization(OrganizationManagementRequest request) {
        AdminPanelManagementResponse response = validateCreationRequest(request);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        Organization entity = new Organization();
        entity.setName(request.getName());
        entity.setEmailAddress(request.getEmailAddress());
        entity.setLocale(Locale.valueOf(request.getLocale()));
        Organization createdOrganization = organizationRepository.save(entity);
        response.setData(AdminPanelUtils.convertOrganization(createdOrganization));
        return response;
    }

    public AdminPanelManagementResponse updateOrganization(String id, OrganizationManagementRequest request) {
        AdminPanelManagementResponse response = validateUpdateClient(id, request);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        try {
            Organization entity = organizationRepository.getById(UUID.fromString(id));
            if (request.getName() != null) {
                entity.setName(request.getName());
            }
            if (request.getLocale() != null) {
                entity.setLocale(Locale.valueOf(request.getLocale()));
            }
            if (request.getEmailAddress() != null) {
                entity.setEmailAddress(request.getEmailAddress());
            }

            Organization updateOrganization = organizationRepository.save(entity);
            response.setData(AdminPanelUtils.convertOrganization(updateOrganization));
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find user by given id=%s", id));
            return response;
        }
    }

    public AdminPanelManagementResponse deleteOrganizationById(String id) {
        AdminPanelManagementResponse response = AdminPanelUtils.validateUUID(id);
        if (response.getHttpCode() != null) {
            return response;
        }

        try {
            organizationRepository.deleteById(UUID.fromString(id));
            webClient.deleteWorkspaceByThirdParty(id);
            return response;
        } catch (Exception e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_DELETE_THIRD_PARTY_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    private AdminPanelManagementResponse validateCreationRequest(OrganizationManagementRequest request) {
        List<String> requiredFields = new ArrayList<>();
        if (StringUtils.isBlank(request.getName())) {
            requiredFields.add("name");
        }
        if (StringUtils.isBlank(request.getLocale())) {
            requiredFields.add("locale");
        }
        if (StringUtils.isBlank(request.getEmailAddress())) {
            requiredFields.add("emailAddress");
        }

        String message = null;
        if (!requiredFields.isEmpty()) {
            message = String.join(",", requiredFields) + " required";
        }

        AdminPanelManagementResponse response = new AdminPanelManagementResponse();
        response.setMessage(message);
        response.setHttpCode(message != null ? HttpStatus.BAD_REQUEST.value() : null);
        response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
        return response;
    }

    private AdminPanelManagementResponse validateUpdateClient(String id, OrganizationManagementRequest request) {
        AdminPanelManagementResponse response = AdminPanelUtils.validateUUID(id);
        if (request == null) {
            response.setHttpCode(HttpStatus.NO_CONTENT.value());
            response.setMessage("No changes");
            return response;
        }
        return response;
    }
}
