package com.tenerity.nordic.entity;

import com.tenerity.nordic.enums.ActionType;
import com.tenerity.nordic.enums.UserRole;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tsu_user_notification")
public class UserNotification {
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "is_read")
    private Boolean isRead;
    @Column(name = "insert_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime insertTime;
    @Column(name = "action_type")
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    @Column(name = "action_object")
    private String actionObject;
    @Column(name = "action_author")
    private String actionAuthor;
    @Column(name = "old_value")
    private String oldValue;
    @Column(name = "new_value")
    private String newValue;
    @Column(name = "case_id")
    private String caseId;
    @Column(name = "case_name")
    private String caseName;

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

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getActionObject() {
        return actionObject;
    }

    public void setActionObject(String actionObject) {
        this.actionObject = actionObject;
    }

    public String getActionAuthor() {
        return actionAuthor;
    }

    public void setActionAuthor(String actionAuthor) {
        this.actionAuthor = actionAuthor;
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
}
