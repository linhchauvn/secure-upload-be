package com.tenerity.nordic.repository;

import com.tenerity.nordic.entity.User;
import com.tenerity.nordic.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findAllByRoleIn(List<UserRole> roles);

    @Query("SELECT u from User u LEFT JOIN u.organization " +
            "WHERE (:keyword is null OR LOWER(u.emailAddress) LIKE LOWER(CONCAT('%', :keyword,'%')) OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword,'%')) OR LOWER(u.organization.name) LIKE LOWER(CONCAT('%', :keyword,'%'))) " +
            "AND u.role IN :role")
    Page<User> searchUserFuzzyWithPagination(@Param("keyword") String keyword,
                                             @Param("role") List<UserRole> roles,
                                             Pageable pageable);

    Page<User> findAllByRoleIn(List<UserRole> roles,
                               Pageable pageable);

    User findUserByUsername(String username);
    User findUserByIdAndRole(UUID id, UserRole role);
    User findUserByIdAndRoleIn(UUID id, List<UserRole> roles);
    Optional<User> findUserByUsernameAndPassword(String username, String password);
    Optional<User> findUserByEmailAddress(String email);
    List<User> findAllByOrganizationId(UUID organizationId);
}
