package com.bankease.controller;

import com.bankease.dto.response.AccountDto;
import com.bankease.dto.response.ApiResponse;
import com.bankease.dto.response.DashboardOverviewDto;
import com.bankease.dto.response.TransactionDto;
import com.bankease.security.SecurityUtils;
import com.bankease.service.AccountService;
import com.bankease.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountDto>>> listAccounts() {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        List<AccountDto> accounts = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardOverviewDto>> getDashboard() {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        DashboardOverviewDto overview = accountService.getDashboardOverview(customerId);
        return ResponseEntity.ok(ApiResponse.success(overview));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDto>> getAccountDetails(@PathVariable("id") Long accountId) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        AccountDto account = accountService.getAccountById(accountId, customerId);
        return ResponseEntity.ok(ApiResponse.success(account));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionDto>>> getAccountTransactions(
            @PathVariable("id") Long accountId,
            Pageable pageable) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        Page<TransactionDto> transactions = transactionService.getAccountTransactions(customerId, accountId, pageable);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/{id}/statement")
    public ResponseEntity<byte[]> downloadStatement(@PathVariable("id") Long accountId) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        byte[] statement = transactionService.generateAccountStatement(customerId, accountId, null, null);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"statement_acc_" + accountId + ".txt\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(statement);
    }
}

