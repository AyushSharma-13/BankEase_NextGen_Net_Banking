package com.bankease.service.impl;

import com.bankease.dto.response.AuditLogDto;
import com.bankease.entity.AuditLog;
import com.bankease.entity.User;
import com.bankease.repository.AuditLogRepository;
import com.bankease.repository.UserRepository;
import com.bankease.security.UserPrincipal;
import com.bankease.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void log(User user, String action, String endpoint, String ipAddress, String details) {
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .endpoint(endpoint)
                .ipAddress(ipAddress != null ? ipAddress : "0.0.0.0")
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional
    public void log(String action, String endpoint, String details) {
        User user = null;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
                user = userRepository.findById(principal.getUserId()).orElse(null);
            }
        } catch (Exception ignored) {
        }
        log(user, action, endpoint, "127.0.0.1", details);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable)
                .map(this::mapToDto);
    }

    private AuditLogDto mapToDto(AuditLog log) {
        return AuditLogDto.builder()
                .auditId(log.getAuditId())
                .username(log.getUser() != null ? log.getUser().getUsername() : "ANONYMOUS/SYSTEM")
                .action(log.getAction())
                .endpoint(log.getEndpoint())
                .ipAddress(log.getIpAddress())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }
}
