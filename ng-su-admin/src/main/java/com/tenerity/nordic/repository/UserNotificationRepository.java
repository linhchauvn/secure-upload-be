package com.tenerity.nordic.repository;

import com.tenerity.nordic.entity.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface UserNotificationRepository extends PagingAndSortingRepository<UserNotification, UUID> {
    Page<UserNotification> findAllByUserId(UUID userId, Pageable pageable);
    Long countByUserIdAndIsReadFalse(UUID userId);
}
