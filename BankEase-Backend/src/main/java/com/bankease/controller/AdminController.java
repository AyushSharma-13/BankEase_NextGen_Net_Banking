package com.bankease.controller;

import com.bankease.dto.response.*;
import com.bankease.service.AdminService;
import com.bankease.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AuditService auditService;

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMetrics() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getSystemMetrics()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllUsers(pageable)));
    }

    @PatchMapping("/users/{id}/toggle")
    public ResponseEntity<ApiResponse<UserDto>> toggleUserStatus(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(ApiResponse.success("User status changed", adminService.toggleUserStatus(userId)));
    }

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<Page<AccountDto>>> getAllAccounts(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllAccounts(pageable)));
    }

    @PatchMapping("/accounts/{id}/status")
    public ResponseEntity<ApiResponse<AccountDto>> updateAccountStatus(
            @PathVariable("id") Long accountId,
            @RequestParam("status") String status) {
        return ResponseEntity.ok(ApiResponse.success("Account status updated", adminService.updateAccountStatus(accountId, status)));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionDto>>> getAllTransactions(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllTransactions(pageable)));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLogDto>>> getAuditLogs(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(auditService.getAllLogs(pageable)));
    }

    @PostMapping("/staff")
    public ResponseEntity<ApiResponse<UserDto>> createStaff(@RequestBody Map<String, String> request) {
        UserDto staff = adminService.createStaffUser(
                request.get("username"),
                request.get("password"),
                request.get("name"),
                request.get("email"),
                request.get("phone")
        );
        return ResponseEntity.ok(ApiResponse.success("Staff user created successfully", staff));
    }
}

