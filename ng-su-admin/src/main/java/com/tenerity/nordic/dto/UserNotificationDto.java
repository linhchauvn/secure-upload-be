package com.tenerity.nordic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tenerity.nordic.enums.ActionType;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserNotificationDto {
    private UUID id;
    private UUID userId;
    private Boolean isRead;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime insertTime;
    private String caseId;
    private String caseName;
    private ActionType actionType;
    private UserNotificationObjectDto actionObject;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public UserNotificationObjectDto getActionObject() {
        return actionObject;
    }

    public void setActionObject(UserNotificationObjectDto actionObject) {
        this.actionObject = actionObject;
    }
}
