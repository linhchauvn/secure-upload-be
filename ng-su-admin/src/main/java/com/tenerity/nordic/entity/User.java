package com.tenerity.nordic.entity;

import com.tenerity.nordic.converter.UserRoleConverter;
import com.tenerity.nordic.enums.Locale;
import com.tenerity.nordic.enums.UserRole;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "tsu_user")
public class User {
    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "email_address")
    private String emailAddress;
    @Column(name = "is_ooo")
    private Boolean isOOO;
    @Column(name = "locale")
    @Enumerated(EnumType.STRING)
    private Locale locale;
    @Column(name = "must_change_password")
    private Boolean mustChangePassword;
    @Column(name = "name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "organization")
    private Organization organization;
    @Column(name = "password_hash")
    private String password;
    @Column(name = "roles")
    @Convert(converter = UserRoleConverter.class)
    private UserRole role;
    @Column(name = "username", unique = true)
    private String username;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Boolean getOOO() {
        return isOOO;
    }

    public void setOOO(Boolean OOO) {
        isOOO = OOO;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Boolean getMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(Boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
