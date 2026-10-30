package com.bankease.service;

import com.bankease.dto.response.AuditLogDto;
import com.bankease.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditService {
    void log(User user, String action, String endpoint, String ipAddress, String details);
    void log(String action, String endpoint, String details);
    Page<AuditLogDto> getAllLogs(Pageable pageable);
}

