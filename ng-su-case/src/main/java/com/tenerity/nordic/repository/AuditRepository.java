package com.tenerity.nordic.repository;

import com.tenerity.nordic.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditRepository extends JpaRepository<Audit, UUID> {
    Audit findFirstByLocalRefAndLatestOrderByLastUpdatedDesc(String localRef, String latest);
}
