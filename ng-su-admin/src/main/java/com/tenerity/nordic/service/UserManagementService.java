package com.tenerity.nordic.service;

import com.tenerity.nordic.client.NgAuthWebClient;
import com.tenerity.nordic.dto.AdminDataResponse;
import com.tenerity.nordic.dto.AdminPanelManagementResponse;
import com.tenerity.nordic.dto.AdminPanelSearchRequest;
import com.tenerity.nordic.dto.AdminPanelSearchResponse;
import com.tenerity.nordic.dto.UserCreateNotificationRequest;
import com.tenerity.nordic.dto.UserDto;
import com.tenerity.nordic.dto.UserManagementRequest;
import com.tenerity.nordic.entity.Organization;
import com.tenerity.nordic.entity.User;
import com.tenerity.nordic.enums.Locale;
import com.tenerity.nordic.enums.UserRole;
import com.tenerity.nordic.repository.OrganizationRepository;
import com.tenerity.nordic.repository.UserRepository;
import com.tenerity.nordic.util.AdminPanelUtils;
import com.tenerity.nordic.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserManagementService {
    Logger logger = LoggerFactory.getLogger(UserManagementService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserNotificationService userNotificationService;
    @Autowired
    private NgAuthWebClient ngAuthWebClient;

    public AdminDataResponse getAllAgents() {
        List<User> entities = userRepository.findAllByRoleIn(Arrays.asList(UserRole.ADMIN, UserRole.AGENT));
        List<UserDto> dtos = entities.stream().map(AdminPanelUtils::convertUser).collect(Collectors.toList());
        AdminDataResponse response = new AdminDataResponse();
        response.setData(dtos);
        return response;
    }

    public AdminPanelSearchResponse searchUser(AdminPanelSearchRequest searchRequest, List<UserRole> roles) {
        AdminPanelSearchResponse response = AdminPanelUtils.validateSearchRequest(searchRequest);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        Pageable pageable = AdminPanelUtils.buildPagingAndSorting(searchRequest.getPage(), searchRequest.getSize(), searchRequest.getSortColumn());
        Page<User> entities;
        if (StringUtils.isBlank(searchRequest.getKeyword())) {
            entities = userRepository.findAllByRoleIn(roles, pageable);
        } else {
            entities = userRepository.searchUserFuzzyWithPagination(searchRequest.getKeyword(), roles, pageable);
        }
        response.setTotalItem(entities.getTotalElements());
        response.setTotalPage(entities.getTotalPages());
        if (entities.getTotalElements() > 0) {
            response.getResults().addAll(entities.get().map(AdminPanelUtils::convertUser).collect(Collectors.toList()));
        }

        return response;
    }

    public AdminPanelManagementResponse findAgentById(String id) {
        AdminPanelManagementResponse response = AdminPanelUtils.validateUUID(id);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        try {
            User entity = userRepository.findUserByIdAndRoleIn(UUID.fromString(id), Arrays.asList(UserRole.ADMIN, UserRole.AGENT));
            response.setData(AdminPanelUtils.convertUser(entity));
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find user by given id=%s", id));
            return response;
        }
    }

    public AdminPanelManagementResponse findThirdPartyUserById(String id) {
        AdminPanelManagementResponse response = AdminPanelUtils.validateUUID(id);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        try {
            User entity = userRepository.findUserByIdAndRole(UUID.fromString(id), UserRole.THIRDPARTY);
            response.setData(AdminPanelUtils.convertUser(entity));
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find user by given id=%s", id));
            return response;
        }
    }

    public AdminPanelManagementResponse  createUser(UserManagementRequest request, boolean isThirdParty) {
        AdminPanelManagementResponse response = validateCreationRequest(request, isThirdParty);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        User entity = new User();
        entity.setEmailAddress(request.getEmailAddress());
        entity.setUsername(request.getUsername());
        if (isThirdParty) {
            entity.setRole(UserRole.THIRDPARTY);
            Optional<Organization> organization = organizationRepository.findById(UUID.fromString(request.getOrganizationId()));
            if (!organization.isPresent()) {
                response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
                response.setMessage(String.format("Cannot find organization by given id=%s", request.getOrganizationId()));
                return response;
            }
            entity.setOrganization(organization.get());
        } else {
            entity.setRole(request.getIsAdmin() ? UserRole.ADMIN : UserRole.AGENT);
            entity.setOOO(request.getIsOutOfOffice());
        }
        entity.setLocale(request.getLocale() != null ? Locale.valueOf(request.getLocale()) : null);
        entity.setMustChangePassword(Boolean.TRUE);
        User createdUser = userRepository.save(entity);

        String authToken = ngAuthWebClient.getNgAuthToken();
        var authUserId = ngAuthWebClient.ngAuthCreateUser(request.getEmailAddress(), request.getUsername(),
                request.getPassword(), createdUser.getId().toString(), authToken, isThirdParty);
        logger.debug("[createUser SUCCESS] authUserId=" + authUserId);
        if (StringUtils.isBlank(authUserId)) {
            userRepository.delete(createdUser);
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode("KEYCLOAK_ERROR");
            response.setMessage("Error while create user.");
            return response;
        }
        response.setData(AdminPanelUtils.convertUser(createdUser));
        return response;
    }

    public AdminPanelManagementResponse updateUser(String id, UserManagementRequest request, boolean isThirdparty) {
        AdminPanelManagementResponse response = validateUpdateUser(id, request, isThirdparty);
        if (StringUtils.isNotBlank(response.getMessage())) {
            return response;
        }

        try {
            User entity = userRepository.getById(UUID.fromString(id));
            if (request.getUsername() != null) {
                entity.setUsername(request.getUsername());
                var updateResult = updateUsernameInKeyCloak(entity.getId().toString(), request.getUsername());
                if (!updateResult) {
                    response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                    response.setCode(Constants.ERROR_CODE_ERROR_UPDATE_USERNAME_KEYCLOAK);
                    response.setMessage("Error while update username");
                    return response;
                }
            }
            if (request.getPassword() != null) {
                var updateResult = updatePasswordInKeyCloak(entity.getUsername(), request.getPassword());
                if (!updateResult) {
                    response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                    response.setCode(Constants.ERROR_CODE_ERROR_UPDATE_PASSWORD_KEYCLOAK);
                    response.setMessage("Error while update password");
                    return response;
                }
            }
            if (request.getEmailAddress() != null) {
                entity.setEmailAddress(request.getEmailAddress());
            }
            if (request.getIsAdmin() != null) {
                entity.setRole(request.getIsAdmin() ? UserRole.ADMIN : UserRole.AGENT);
            }
            if (request.getIsOutOfOffice() != null) {
                entity.setOOO(request.getIsOutOfOffice());
            }
            if (request.getLocale() != null) {
                entity.setLocale(Locale.valueOf(request.getLocale()));
            }
            if (UserRole.THIRDPARTY == entity.getRole() && StringUtils.isNotBlank(request.getOrganizationId())) {
                Optional<Organization> organization =  organizationRepository.findById(UUID.fromString(request.getOrganizationId()));
                if (organization.isPresent()) {
                    entity.setOrganization(organization.get());
                }
            }

            User updatedUser = userRepository.save(entity);
            if (request.getIsOutOfOffice() != null && request.getIsOutOfOffice()) {
                createAgentIsOOONotification(updatedUser);
            }

            response.setData(AdminPanelUtils.convertUser(updatedUser));
            return response;
        } catch (EntityNotFoundException ee) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find user by given id=%s", id));
            return response;
        }
    }

    public AdminPanelManagementResponse deleteUserById(String id) {
        AdminPanelManagementResponse response = AdminPanelUtils.validateUUID(id);
        if (response.getHttpCode() != null) {
            return response;
        }

        try {
            userRepository.deleteById(UUID.fromString(id));
            var authToken = ngAuthWebClient.getNgAuthToken();
            ngAuthWebClient.ngAuthDeleteUser(id, authToken);
            return response;
        } catch (EmptyResultDataAccessException e) {
            response.setHttpCode(HttpStatus.NOT_FOUND.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find user by given id=%s", id));
            return response;
        }
    }

    private AdminPanelManagementResponse validateUpdateUser(String id, UserManagementRequest request, boolean isThirdparty) {
        AdminPanelManagementResponse response = AdminPanelUtils.validateUUID(id);
        if (request == null) {
            response.setHttpCode(HttpStatus.NO_CONTENT.value());
            response.setMessage("No changes");
            return response;
        }
        if (StringUtils.isNotBlank(request.getUsername())) {
            User user = userRepository.findUserByUsername(request.getUsername());
            if (user != null) {
                response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                response.setCode(Constants.ERROR_CODE_EXISTED_USERNAME);
                response.setMessage(String.format("username %s already exists!", request.getUsername()));
                return response;
            }
        }
        if(!isThirdparty && StringUtils.isNotBlank(request.getEmailAddress())){
            Optional<User> user = userRepository.findUserByEmailAddress(request.getEmailAddress());
            if (user.isPresent()) {
                response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                response.setCode(Constants.ERROR_CODE_EXISTED_EMAIL);
                response.setMessage(String.format("Email address %s already exists!", request.getEmailAddress()));
                return response;
            }
        }
        return response;
    }

    private AdminPanelManagementResponse validateCreationRequest(UserManagementRequest request, boolean isThirdParty) {
        List<String> requiredFields = new ArrayList<>();
        if (StringUtils.isBlank(request.getUsername())) {
            requiredFields.add("username");
        }
        if (StringUtils.isBlank(request.getPassword())) {
            requiredFields.add("password");
        }
        if (StringUtils.isBlank(request.getEmailAddress()) && isThirdParty) {
            requiredFields.add("emailAddress");
        }
        if (isThirdParty) {
            if (StringUtils.isBlank(request.getOrganizationId())) {
                requiredFields.add("organizationId");
            }
        } else {
            if (request.getIsAdmin() == null) {
                requiredFields.add("isAdmin");
            }
            if (request.getIsOutOfOffice() == null) {
                requiredFields.add("outOfOffice");
            }
        }

        String message = null;
        String errCode = null;
        if (!requiredFields.isEmpty()) {
            message = String.join(",", requiredFields) + " required";
        }
        if (message == null) {
            User user = userRepository.findUserByUsername(request.getUsername());
            if (user != null) {
                message = "Username " + request.getUsername() + " is exists!";
                errCode = Constants.ERROR_CODE_EXISTED_USERNAME;
            }

            if (isThirdParty) {
                try {
                    Optional<Organization> entity = organizationRepository.findById(UUID.fromString(request.getOrganizationId()));
                    if (!entity.isPresent()) {
                        message = "Cannot find organization by given id=" + request.getOrganizationId();
                    }
                } catch (NullPointerException | IllegalArgumentException e) {
                    message = "Please provide correct UUID. Received input: " + request.getOrganizationId();
                }
            }
        }

        if(message == null && !isThirdParty){
            Optional<User> user = userRepository.findUserByEmailAddress(request.getEmailAddress());
            if (user.isPresent()) {
                message = "Email address " + request.getUsername() + " is exists!";
                errCode = Constants.ERROR_CODE_EXISTED_EMAIL;
            }
        }

        AdminPanelManagementResponse response = new AdminPanelManagementResponse();
        response.setMessage(message);
        response.setHttpCode(message != null ? HttpStatus.BAD_REQUEST.value() : null);
        response.setCode(errCode != null ? errCode : Constants.ERROR_CODE_INVALID_INPUT);
        return response;
    }

    private void createAgentIsOOONotification(User user) {
        List<User> entities = userRepository.findAllByRoleIn(Arrays.asList(UserRole.ADMIN, UserRole.AGENT));
        List<UUID> notifyUserid = entities.stream().filter(item -> item.getId() != user.getId())
                .map(item -> item.getId()).collect(Collectors.toList());
        UserCreateNotificationRequest request = new UserCreateNotificationRequest();
        request.setNotifyIds(notifyUserid);
        request.setActionType("OOO");
        request.setActionObject(user.getUsername());
        userNotificationService.createNotification(request);
    }

    private boolean updateUsernameInKeyCloak(String userId, String username) {
        String ngAuthToken = ngAuthWebClient.getNgAuthToken();
        if (ngAuthToken != null) {
            var responseMsg = ngAuthWebClient.ngAuthUpdateUsername(userId, username, ngAuthToken);
            return responseMsg;
        }
        return false;
    }

    private boolean updatePasswordInKeyCloak(String username, String newPassword) {
        String ngAuthToken = ngAuthWebClient.getNgAuthToken();
        if (ngAuthToken != null) {
            var responseMsg = ngAuthWebClient.ngAuthUpdatePassword(username, newPassword, ngAuthToken);
            return responseMsg;
        }
        return false;
    }
}
