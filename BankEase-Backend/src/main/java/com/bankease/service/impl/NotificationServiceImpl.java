package com.bankease.service.impl;

import com.bankease.dto.response.NotificationDto;
import com.bankease.entity.Customer;
import com.bankease.entity.Notification;
import com.bankease.exception.ResourceNotFoundException;
import com.bankease.exception.UnauthorizedAccessException;
import com.bankease.repository.CustomerRepository;
import com.bankease.repository.NotificationRepository;
import com.bankease.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public NotificationDto createNotification(Long customerId, String title, String message, String type) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Notification notification = Notification.builder()
                .customer(customer)
                .title(title)
                .message(message)
                .type(type != null ? type : "SYSTEM")
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getCustomerNotifications(Long customerId) {
        return notificationRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getCustomerNotifications(Long customerId, Pageable pageable) {
        return notificationRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long customerId) {
        return notificationRepository.countByCustomerCustomerIdAndIsReadFalse(customerId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long customerId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (!notification.getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("You are not authorized to view or update this notification");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long customerId) {
        notificationRepository.markAllAsRead(customerId);
    }

    private NotificationDto mapToDto(Notification n) {
        return NotificationDto.builder()
                .notificationId(n.getNotificationId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
