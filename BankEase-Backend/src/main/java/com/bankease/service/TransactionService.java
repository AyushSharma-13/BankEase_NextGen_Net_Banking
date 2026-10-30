package com.bankease.service;

import com.bankease.dto.response.TransactionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Page<TransactionDto> getTransactions(Long customerId, String type, String status,
                                         LocalDateTime startDate, LocalDateTime endDate,
                                         String search, Pageable pageable);
    Page<TransactionDto> getAccountTransactions(Long customerId, Long accountId, Pageable pageable);
    TransactionDto getTransactionByReference(Long customerId, String referenceNo);
    byte[] generateAccountStatement(Long customerId, Long accountId, LocalDateTime from, LocalDateTime to);
}
