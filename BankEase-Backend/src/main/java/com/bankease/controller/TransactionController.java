package com.bankease.controller;

import com.bankease.dto.response.ApiResponse;
import com.bankease.dto.response.TransactionDto;
import com.bankease.security.SecurityUtils;
import com.bankease.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionDto>>> getTransactions(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(value = "search", required = false) String search,
            Pageable pageable) {

        Long customerId = SecurityUtils.getCurrentCustomerId();
        Page<TransactionDto> result = transactionService.getTransactions(customerId, type, status, startDate, endDate, search, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{referenceNo}")
    public ResponseEntity<ApiResponse<TransactionDto>> getTransactionByReference(@PathVariable("referenceNo") String referenceNo) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        TransactionDto txn = transactionService.getTransactionByReference(customerId, referenceNo);
        return ResponseEntity.ok(ApiResponse.success(txn));
    }
}
