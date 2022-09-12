package com.tenerity.nordic.service;

import com.tenerity.nordic.dto.UserCreateNotificationRequest;
import com.tenerity.nordic.dto.UserNotificationDto;
import com.tenerity.nordic.dto.UserNotificationObjectDto;
import com.tenerity.nordic.dto.UserNotificationStatisticResponse;
import com.tenerity.nordic.dto.UserSearchNotificationRequest;
import com.tenerity.nordic.dto.UserNotificationResponse;
import com.tenerity.nordic.entity.User;
import com.tenerity.nordic.entity.UserNotification;
import com.tenerity.nordic.enums.ActionType;
import com.tenerity.nordic.repository.UserNotificationRepository;
import com.tenerity.nordic.repository.UserRepository;
import com.tenerity.nordic.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserNotificationService {

    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private UserRepository userRepository;

    public UserNotificationResponse markAsRead(String id) {
        UserNotificationResponse response = new UserNotificationResponse();
        try {
            var optional = userNotificationRepository.findById(UUID.fromString(id));
            if (optional.isPresent()) {
                var entity = optional.get();
                entity.setRead(Boolean.TRUE);
                userNotificationRepository.save(entity);
            }
        } catch (Exception e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setCode(Constants.ERROR_CODE_READ_NOTIFICATION_ERROR);
        }

        return response;
    }

    public UserNotificationStatisticResponse getNotificationStatistic(String userId) {
        UserNotificationStatisticResponse response = new UserNotificationStatisticResponse();

        try {
            long number = userNotificationRepository.countByUserIdAndIsReadFalse(UUID.fromString(userId));
            response.setUnreadNotification(number);
        } catch (Exception e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            response.setCode(Constants.ERROR_CODE_NOTIFICATION_STATISTIC_ERROR);
        }
        return response;
    }

    public UserNotificationResponse getNotification(UserSearchNotificationRequest request) {
        UserNotificationResponse response = validateInput(request);
        if (response.getCode() != null) {
            return response;
        }

        Pageable sortedByInsertTimeDesc = PageRequest.of(request.getPage(), request.getSize(), Sort.by("insertTime").descending());
        var notificationPage = userNotificationRepository.findAllByUserId(UUID.fromString(request.getUserId()), sortedByInsertTimeDesc);
        var dto = notificationPage.getContent().stream().map(UserNotificationService::convertUserNotification).collect(Collectors.toList());
        response.setResults(dto);
        response.setTotalItem(notificationPage.getTotalElements());
        response.setTotalPage(notificationPage.getTotalPages());
        return response;
    }

    public UserNotificationResponse createNotification(UserCreateNotificationRequest request) {
        UserNotificationResponse response = validateInput(request);
        if (response.getCode() != null) {
            return response;
        }

        List<UUID> userIds = new ArrayList<>();
        if (request.getNotifyIds() != null) {
            userIds.addAll(request.getNotifyIds());
        } else if (request.getOrganizationId() != null) {
            var tpUsers = userRepository.findAllByOrganizationId(request.getOrganizationId());
            if (tpUsers != null && !tpUsers.isEmpty()) {
                userIds.addAll(tpUsers.stream().map(tp -> tp.getId()).collect(Collectors.toList()));
            }
        }
        User author = null;
        if (request.getActionAuthorId() != null) {
            var optional = userRepository.findById(UUID.fromString(request.getActionAuthorId()));
            if (optional.isPresent()) {
                author = optional.get();
            }
        }

        List<UserNotification> updateEntities = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        if (!userIds.isEmpty()) {
            User finalAuthor = author;
            userIds.forEach(userId -> {
                var entity = new UserNotification();
                entity.setUserId(userId);
                entity.setCaseId(request.getCaseId());
                entity.setCaseName(request.getCaseName());
                entity.setRead(Boolean.FALSE);
                entity.setInsertTime(now);
                entity.setActionType(ActionType.valueOf(request.getActionType()));
                entity.setActionObject(request.getActionObject());
                if (finalAuthor != null) {
                    entity.setActionAuthor(finalAuthor.getUsername());
                }
                else {
                    entity.setActionAuthor("customer");
                }
                entity.setOldValue(request.getOldValue());
                entity.setNewValue(request.getNewValue());
                updateEntities.add(entity);
            });
        }

        userNotificationRepository.saveAll(updateEntities);
        return response;
    }

    private UserNotificationResponse validateInput(UserCreateNotificationRequest request) {
        UserNotificationResponse response = new UserNotificationResponse();
        if (request.getNotifyIds() == null && request.getOrganizationId() == null) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setMessage("Missing parameters");
            return response;
        }

        return response;
    }

    private UserNotificationResponse validateInput(UserSearchNotificationRequest request) {
        UserNotificationResponse response = new UserNotificationResponse();
        if (StringUtils.isBlank(request.getUserId()) || request.getPage() == null || request.getSize() == null) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setMessage("Missing parameters");
            return response;
        }
        try {
            UUID.fromString(request.getUserId());
        } catch (IllegalArgumentException e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setMessage("Invalid format: " + e.getMessage());
            return response;
        }
        return response;
    }

    private static UserNotificationDto convertUserNotification(UserNotification entity) {
        if (entity == null) {
            return null;
        }

        UserNotificationDto dto = new UserNotificationDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setIsRead(entity.getRead());
        dto.setInsertTime(entity.getInsertTime());
        dto.setActionType(entity.getActionType());
        dto.setCaseId(entity.getCaseId());
        dto.setCaseName(entity.getCaseName());
        dto.setActionObject(convertActionObject(entity));
        return dto;
    }

    private static UserNotificationObjectDto convertActionObject(UserNotification entity) {
        var dto = new UserNotificationObjectDto();
        switch (entity.getActionType()) {
            case ASSIGN:
                dto.setAgentName(entity.getActionAuthor());
                break;
            case DELETE:
                dto.setThirdpartyWorkspaceName(entity.getActionObject());
                break;
            case UPLOAD:
                dto.setDocumentName(entity.getActionObject());
                dto.setAgentName(entity.getActionAuthor());
                break;
            case SHARE:
                dto.setDocumentName(entity.getActionObject());
                dto.setAgentName(entity.getActionAuthor());
                break;
            case EDIT:
                dto.setOldDocumentTitle(entity.getOldValue());
                dto.setNewDocumentTitle(entity.getNewValue());
                if (entity.getActionAuthor() != null) {
                    dto.setThirdparty(entity.getActionAuthor());
                } else {
                    dto.setCustomer("customer");
                }
                break;
            case REQUESTESIGN:
                dto.setAgentName(entity.getActionAuthor());
                dto.setDocumentName(entity.getActionObject());
                break;
            case CUSTOMERESIGN:
                dto.setCustomerWorkspaceName(entity.getActionObject());
                break;
            case OOO:
                dto.setAgentName(entity.getActionObject());
                break;
        }
        return dto;
    }
}
