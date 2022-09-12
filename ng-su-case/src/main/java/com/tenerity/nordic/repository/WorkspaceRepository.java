package com.tenerity.nordic.repository;

import com.tenerity.nordic.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    List<Workspace> findAllByThirdPartyId(UUID thirdPartyId);
    Optional<Workspace> findByIdAndCustomerTokenHash(UUID id, String tokenHash);
    Optional<Workspace> findByIdAndCaseeCustomerNationalId(UUID id, String nationalId);
}
