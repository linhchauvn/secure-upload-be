package com.tenerity.nordic.repository;

import com.tenerity.nordic.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    @Query("SELECT o from Organization o WHERE :keyword is null OR LOWER(o.name) LIKE LOWER(CONCAT('%', :keyword,'%')) OR o.emailAddress LIKE LOWER(CONCAT('%', :keyword,'%'))")
    Page<Organization> searchOrganizationFuzzyWithPagination(@Param("keyword") String keyword,
                                                             Pageable pageable);
}
