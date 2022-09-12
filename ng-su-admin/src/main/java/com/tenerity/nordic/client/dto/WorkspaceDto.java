package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tenerity.nordic.dto.ClientDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkspaceDto {
    private UUID id;
    private Boolean bankIdLogin;
    private Boolean belongToCustomer;
    private String code;
    private String customerTokenHash;
    private String documentIds;
    private List<DocumentDto> documents;
    private String label;
    private UUID thirdPartyId;
    private String thirdPartyName;
    private UUID thirdPartyRef;
    private UUID caseId;
    private String superOfficeId;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastAccess;
    private ClientDto client;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getBankIdLogin() {
        return bankIdLogin;
    }

    public void setBankIdLogin(Boolean bankIdLogin) {
        this.bankIdLogin = bankIdLogin;
    }

    public Boolean getBelongToCustomer() {
        return belongToCustomer;
    }

    public void setBelongToCustomer(Boolean belongToCustomer) {
        this.belongToCustomer = belongToCustomer;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCustomerTokenHash() {
        return customerTokenHash;
    }

    public void setCustomerTokenHash(String customerTokenHash) {
        this.customerTokenHash = customerTokenHash;
    }

    public String getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(String documentIds) {
        this.documentIds = documentIds;
    }

    public List<DocumentDto> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDto> documents) {
        this.documents = documents;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public UUID getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(UUID thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public String getThirdPartyName() {
        return thirdPartyName;
    }

    public void setThirdPartyName(String thirdPartyName) {
        this.thirdPartyName = thirdPartyName;
    }

    public UUID getThirdPartyRef() {
        return thirdPartyRef;
    }

    public void setThirdPartyRef(UUID thirdPartyRef) {
        this.thirdPartyRef = thirdPartyRef;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public void setCaseId(UUID caseId) {
        this.caseId = caseId;
    }

    public String getSuperOfficeId() {
        return superOfficeId;
    }

    public void setSuperOfficeId(String superOfficeId) {
        this.superOfficeId = superOfficeId;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public ClientDto getClient() {
        return client;
    }

    public void setClient(ClientDto client) {
        this.client = client;
    }
}
