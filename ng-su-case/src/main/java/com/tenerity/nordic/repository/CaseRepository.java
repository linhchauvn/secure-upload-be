package com.tenerity.nordic.repository;

import com.tenerity.nordic.entity.Case;
import com.tenerity.nordic.enums.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CaseRepository extends JpaRepository<Case, UUID> {
    Optional<Case> findBySuperOfficeIDIgnoreCase(String superOfficeId);
    List<Case> findAllByClientId(UUID clientId);

    @Query("select c from Case c where c.lastUpdated <= :date AND c.status=:status AND c.needAgentNotification = true")
    List<Case> getAllSeventyTwoAgo(@Param("date")LocalDateTime date, @Param("status") CaseStatus status);

    @Query("select c from Case c where c.lastUpdated BETWEEN :startDate AND :endDate AND c.status=:status AND c.needAgentNotification = true")
    List<Case> getAllBetweenDates(@Param("startDate")LocalDateTime startDate,
                                         @Param("endDate")LocalDateTime endDate,
                                        @Param("status") CaseStatus status);

    // List<Case> findAllByLastUpdatedBetween(LocalDateTime startDate, LocalDateTime endDate);

}
