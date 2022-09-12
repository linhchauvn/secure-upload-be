package com.tenerity.nordic.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tsu_workspace")
public class Workspace {
    @Id
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "bank_id_login")
    private Boolean bankIdLogin;
    @Column(name = "belongs_to_customer")
    private Boolean belongToCustomer;
    @Column(name = "code")
    private String code;
    @Column(name = "customer_token_hash")
    private String customerTokenHash;
    @Column(name = "documents")
    private String documents;
    @Column(name = "label")
    private String label;
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @Column(name = "third_party_id")
    private UUID thirdPartyId;
    @Type(type="org.hibernate.type.PostgresUUIDType")
    @Column(name = "third_party_ref")
    private UUID thirdPartyRef;
    @ManyToOne
    @JoinColumn(name = "case_id")
    private Case casee;
    @Column(name = "last_access", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastAccess;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "tsu_workspace_document", joinColumns = @JoinColumn(name = "workspace_id"))
//    @AttributeOverrides({
//            @AttributeOverride(name = "workspaceId", column = @Column(name = "workspace_id")),
//            @AttributeOverride(name = "documentId", column = @Column(name = "document_id"))
//    })
//    @OneToMany(mappedBy="workspace_id", fetch=FetchType.EAGER)
//    private List<WorkspaceDocument> documentIds;

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

    public String getDocuments() {
        return documents;
    }

    public void setDocuments(String documents) {
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

    public UUID getThirdPartyRef() {
        return thirdPartyRef;
    }

    public void setThirdPartyRef(UUID thirdPartyRef) {
        this.thirdPartyRef = thirdPartyRef;
    }

    public Case getCasee() {
        return casee;
    }

    public void setCasee(Case casee) {
        this.casee = casee;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }
//
//    public List<WorkspaceDocument> getDocumentIds() {
//        return documentIds;
//    }
//
//    public void setDocumentIds(List<WorkspaceDocument> documentIds) {
//        this.documentIds = documentIds;
//    }
}
