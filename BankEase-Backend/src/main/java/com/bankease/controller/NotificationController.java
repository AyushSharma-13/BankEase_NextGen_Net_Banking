package com.bankease.controller;

import com.bankease.dto.response.ApiResponse;
import com.bankease.dto.response.NotificationDto;
import com.bankease.security.SecurityUtils;
import com.bankease.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationDto>>> getNotifications(Pageable pageable) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        Page<NotificationDto> notifications = notificationService.getCustomerNotifications(customerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount() {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        long count = notificationService.getUnreadCount(customerId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("unreadCount", count)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable("id") Long notificationId) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        notificationService.markAsRead(notificationId, customerId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        notificationService.markAllAsRead(customerId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}
