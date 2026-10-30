package com.bankease.service;

import com.bankease.dto.response.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    NotificationDto createNotification(Long customerId, String title, String message, String type);
    List<NotificationDto> getCustomerNotifications(Long customerId);
    Page<NotificationDto> getCustomerNotifications(Long customerId, Pageable pageable);
    long getUnreadCount(Long customerId);
    void markAsRead(Long notificationId, Long customerId);
    void markAllAsRead(Long customerId);
}

