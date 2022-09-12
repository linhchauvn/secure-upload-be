package com.tenerity.nordic.service;

import com.tenerity.nordic.dto.AuditCreationRequest;
import com.tenerity.nordic.dto.AuditCreationResponse;
import com.tenerity.nordic.dto.AuditDto;
import com.tenerity.nordic.entity.Audit;
import com.tenerity.nordic.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {
    @Autowired
    private AuditRepository auditRepository;

    public AuditCreationResponse createAuditData(AuditCreationRequest request) {
        AuditCreationResponse response = new AuditCreationResponse();
        var entity = new Audit();
        entity.setUriAccessed(request.getUriAccessed());
        entity.setRemoteAddr(request.getRemoteAddr());
        entity.setLocalRef(request.getLocalRef());
        entity.setLatest(request.getLatest() ? "true" : "false");
        entity.setLastUpdated(LocalDateTime.now());
        entity = auditRepository.save(entity);

        response.setData(convertEntity(entity));
        return response;
    }

    public AuditDto getLatestAudit(String localRef) {
        var entity = auditRepository.findFirstByLocalRefAndLatestOrderByLastUpdatedDesc(localRef, "true");
        return convertEntity(entity);
    }

    private AuditDto convertEntity(Audit entity) {
        if (entity == null) {
            return null;
        }
        var dto = new AuditDto();
        dto.setId(entity.getId());
        dto.setUriAccessed(entity.getUriAccessed());
        dto.setRemoteAddr(entity.getRemoteAddr());
        dto.setLocalRef(entity.getLocalRef());
        dto.setLatest(entity.getLatest());
        dto.setLastUpdated(entity.getLastUpdated());
        return dto;
    }

}
