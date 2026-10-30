package com.bankease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long transactionId;
    private Long accountId;
    private String accountNo;
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String status;
    private String description;
    private String referenceNo;
    private LocalDateTime createdAt;
}

