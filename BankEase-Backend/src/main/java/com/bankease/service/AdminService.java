package com.bankease.service;

import com.bankease.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AdminService {
    Map<String, Object> getSystemMetrics();
    Page<UserDto> getAllUsers(Pageable pageable);
    UserDto toggleUserStatus(Long userId);
    Page<AccountDto> getAllAccounts(Pageable pageable);
    AccountDto updateAccountStatus(Long accountId, String status);
    Page<TransactionDto> getAllTransactions(Pageable pageable);
    UserDto createStaffUser(String username, String password, String name, String email, String phone);
}
