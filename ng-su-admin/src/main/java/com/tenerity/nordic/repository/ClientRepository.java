package com.tenerity.nordic.repository;

import com.tenerity.nordic.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    @Query("SELECT c from Client c WHERE :keyword is null OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword,'%'))")
    Page<Client> searchClientFuzzyWithPagination(@Param("keyword") String keyword,
                                                 Pageable pageable);

}
