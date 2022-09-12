package com.tenerity.nordic.service;

import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.dto.AdminDataResponse;
import com.tenerity.nordic.dto.AdminPanelManagementResponse;
import com.tenerity.nordic.dto.AdminPanelSearchRequest;
import com.tenerity.nordic.dto.AdminPanelSearchResponse;
import com.tenerity.nordic.dto.ClientDto;
import com.tenerity.nordic.dto.ClientManagementRequest;
import com.tenerity.nordic.entity.Client;
import com.tenerity.nordic.enums.Locale;
import com.tenerity.nordic.repository.ClientRepository;
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
public class ClientManagementService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private InternalWebClient webClient;

    public AdminDataResponse getAllClients() {
        List<Client> entities = clientRepository.findAll();
        List<ClientDto> dtos = entities.stream().map(AdminPanelUtils::convertClient).collect(Collectors.toList());
        AdminDataResponse response = new AdminDataResponse();
        response.setData(dtos);
        return response;
    }

    public AdminPanelSearchResponse searchClient(AdminPanelSearchRequest searchRequest) {
        AdminPanelSearchResponse response = AdminPanelUtils.validateSearchRequest(searchRequest);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        Pageable pageable = AdminPanelUtils.buildPagingAndSorting(searchRequest.getPage(), searchRequest.getSize(), searchRequest.getSortColumn());
        Page<Client> entities;
        if (StringUtils.isBlank(searchRequest.getKeyword())) {
            entities = clientRepository.findAll(pageable);
        } else {
            entities = clientRepository.searchClientFuzzyWithPagination(searchRequest.getKeyword(), pageable);
        }
        response.setTotalItem(entities.getTotalElements());
        response.setTotalPage(entities.getTotalPages());
        if (entities.getTotalElements() > 0) {
            response.getResults().addAll(entities.get().map(AdminPanelUtils::convertClient).collect(Collectors.toList()));
        }

        return response;
    }

    public AdminPanelManagementResponse findClientById(String id) {
        AdminPanelManagementResponse response = AdminPanelUtils.validateUUID(id);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        try {
            Client entity = clientRepository.getById(UUID.fromString(id));
            response.setData(AdminPanelUtils.convertClient(entity));
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find client by given id=%s", id));
            return response;
        }
    }

    public AdminPanelManagementResponse createClient(ClientManagementRequest request) {
        AdminPanelManagementResponse response = validateCreationRequest(request);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        Client entity = new Client();
        entity.setName(request.getName());
        entity.setLocale(Locale.valueOf(request.getLocale()));
        entity.setNavColour(request.getBrandColour());
        Client createdClient = clientRepository.save(entity);
        response.setData(AdminPanelUtils.convertClient(createdClient));
        return response;
    }

    public AdminPanelManagementResponse updateClient(String id, ClientManagementRequest request) {
        AdminPanelManagementResponse response = validateUpdateClient(id, request);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        try {
            Client entity = clientRepository.getById(UUID.fromString(id));
            if (request.getName() != null) {
                entity.setName(request.getName());
            }
            if (request.getLocale() != null) {
                entity.setLocale(Locale.valueOf(request.getLocale()));
            }
            if (request.getBrandColour() != null) {
                entity.setNavColour(request.getBrandColour());
            }

            Client updateClient = clientRepository.save(entity);
            response.setData(AdminPanelUtils.convertClient(updateClient));
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find user by given id=%s", id));
            return response;
        }
    }

    public AdminPanelManagementResponse deleteClientById(String id) {
        AdminPanelManagementResponse response = AdminPanelUtils.validateUUID(id);
        if (response.getHttpCode() != null) {
            return response;
        }

        try {
            clientRepository.deleteById(UUID.fromString(id));
            webClient.deleteCaseByClient(id);
            return response;
        } catch (Exception e) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_DELETE_CLIENT_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    private AdminPanelManagementResponse validateCreationRequest(ClientManagementRequest request) {
        List<String> requiredFields = new ArrayList<>();
        if (StringUtils.isBlank(request.getName())) {
            requiredFields.add("name");
        }
        if (StringUtils.isBlank(request.getLocale())) {
            requiredFields.add("locale");
        }
        if (StringUtils.isBlank(request.getBrandColour())) {
            requiredFields.add("brandColour");
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

    private AdminPanelManagementResponse validateUpdateClient(String id, ClientManagementRequest request) {
        AdminPanelManagementResponse response = AdminPanelUtils.validateUUID(id);
        if (request == null) {
            response.setHttpCode(HttpStatus.NO_CONTENT.value());
            response.setMessage("No changes");
            return response;
        }
        return response;
    }
}
