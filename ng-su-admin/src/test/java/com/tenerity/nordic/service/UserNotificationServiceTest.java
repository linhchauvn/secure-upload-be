package com.tenerity.nordic.service;

import com.tenerity.nordic.dto.UserCreateNotificationRequest;
import com.tenerity.nordic.dto.UserSearchNotificationRequest;
import com.tenerity.nordic.entity.UserNotification;
import com.tenerity.nordic.enums.ActionType;
import com.tenerity.nordic.repository.UserNotificationRepository;
import com.tenerity.nordic.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserNotificationServiceTest {

    @Mock
    private UserNotificationRepository userNotificationRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserNotificationService userNotificationService;

    @Test
    void markAsRead_happyCase() {
        UUID id = UUID.randomUUID();
        var user = new UserNotification();
        user.setId(id);
        when(userNotificationRepository.findById(id)).thenReturn(Optional.of(user));

        var res = userNotificationService.markAsRead(id.toString());
        assertNotNull(res);
        assertNull(res.getMessage());
    }

    @Test
    void getNotificationStatistic_happyCase() {
        UUID id = UUID.randomUUID();
        long number = 10L;
        when(userNotificationRepository.countByUserIdAndIsReadFalse(id)).thenReturn(number);

        var res = userNotificationService.getNotificationStatistic(id.toString());
        assertNotNull(res);
        assertEquals(number, res.getUnreadNotification());
    }

    @Test
    void getNotification_happyCase() {
        UUID id = UUID.randomUUID();
        var req = new UserSearchNotificationRequest();
        req.setUserId(id.toString());
        req.setPage(0);
        req.setSize(10);
        var user = new UserNotification();
        user.setId(id);
        user.setActionType(ActionType.OOO);
        var page = new PageImpl<>(Arrays.asList(user));
        when(userNotificationRepository.findAllByUserId(eq(id), any())).thenReturn(page);

        var res = userNotificationService.getNotification(req);
        assertNotNull(res);
        assertEquals(1L, res.getTotalItem());
    }

    @Test
    void createNotification_happyCase() {
        UUID id = UUID.randomUUID();
        var req = new UserCreateNotificationRequest();
        req.setNotifyIds(Arrays.asList(id));
        req.setActionType("OOO");
        req.setActionObject("username");

        var res = userNotificationService.createNotification(req);
        assertNotNull(res);
        assertNull(res.getMessage());
        ArgumentCaptor<List<UserNotification>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(userNotificationRepository).saveAll(listCaptor.capture());
        assertEquals(req.getActionObject(), listCaptor.getValue().get(0).getActionObject());
    }
}
