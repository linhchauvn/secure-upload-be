package com.tenerity.nordic.client.dto;

import java.util.List;
import java.util.UUID;

public class UserCreateNotificationRequest {
    private List<UUID> notifyIds; //workspaceId/userId
    private UUID organizationId;
    private String caseId;
    private String caseName;
    private String actionType; //CREATE, UPLOAD, EDIT, DELETE, ASSIGN, REQUEST, CLOSE, OOO;
    private String actionObject;
    private String actionAuthorId;
    private String oldValue;
    private String newValue;

    public List<UUID> getNotifyIds() {
        return notifyIds;
    }

    public void setNotifyIds(List<UUID> notifyIds) {
        this.notifyIds = notifyIds;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
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

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionObject() {
        return actionObject;
    }

    public void setActionObject(String actionObject) {
        this.actionObject = actionObject;
    }

    public String getActionAuthorId() {
        return actionAuthorId;
    }

    public void setActionAuthorId(String actionAuthorId) {
        this.actionAuthorId = actionAuthorId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
