package com.tenerity.nordic.entity;

import com.tenerity.nordic.enums.CaseStatus;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tsu_case")
public class Case {
    @Id
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "superoffice_id", unique = true)
    private String superOfficeID;
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @Column(name = "assigned_agent")
    private UUID assignedAgent;
    @Column(name = "last_updated", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastUpdated;
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @Column(name = "client")
    private UUID clientId;
    @Column(name = "customer_email")
    private String customerEmail;
    @Column(name = "code")
    private String code;
    @Column(name = "customer_date_of_birth")
    private String customerDOB;
    @Column(name = "customer_first_name")
    private String customerFirstName;
    @Column(name = "customer_last_name")
    private String customerLastName;
    @Column(name = "customer_national_id")
    private String customerNationalId;
    @Column(name = "customer_token_hash")
    private String customerTokenHash;
    @Column(name = "documents_expunged")
    private Boolean documentsExpunged;
    @Column(name = "need_agent_notification")
    private Boolean needAgentNotification;
    @Column (name = "status")
    @Type(type="com.tenerity.nordic.entity.databasetype.EnumTypePostgreSql")
    private CaseStatus status;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "casee", cascade = CascadeType.ALL)
    private List<Workspace> workspaces;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSuperOfficeID() {
        return superOfficeID;
    }

    public void setSuperOfficeID(String superOfficeID) {
        this.superOfficeID = superOfficeID;
    }

    public UUID getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(UUID assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID client) {
        this.clientId = client;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCustomerDOB() {
        return customerDOB;
    }

    public void setCustomerDOB(String customerDOB) {
        this.customerDOB = customerDOB;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerNationalId() {
        return customerNationalId;
    }

    public void setCustomerNationalId(String customerNationalId) {
        this.customerNationalId = customerNationalId;
    }

    public String getCustomerTokenHash() {
        return customerTokenHash;
    }

    public void setCustomerTokenHash(String customerTokenHash) {
        this.customerTokenHash = customerTokenHash;
    }

    public Boolean getDocumentsExpunged() {
        return documentsExpunged;
    }

    public void setDocumentsExpunged(Boolean documentsExpunged) {
        this.documentsExpunged = documentsExpunged;
    }

    public Boolean getNeedAgentNotification() {
        return needAgentNotification;
    }

    public void setNeedAgentNotification(Boolean needAgentNotification) {
        this.needAgentNotification = needAgentNotification;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public List<Workspace> getWorkspaces() {
        return workspaces;
    }

    public void setWorkspaces(List<Workspace> workspaces) {
        this.workspaces = workspaces;
    }
}
