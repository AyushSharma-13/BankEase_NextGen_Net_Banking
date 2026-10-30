package com.bankease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private Long auditId;
    private String username;
    private String action;
    private String endpoint;
    private String ipAddress;
    private String details;
    private LocalDateTime timestamp;
}
