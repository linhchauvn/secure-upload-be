package com.tenerity.nordic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserNotificationStatisticResponse extends EntityResponse {
    private Long unreadNotification;

    public Long getUnreadNotification() {
        return unreadNotification;
    }

    public void setUnreadNotification(Long unreadNotification) {
        this.unreadNotification = unreadNotification;
    }
}
