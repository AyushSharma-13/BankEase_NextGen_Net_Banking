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
public class AccountDto {
    private Long accountId;
    private Long customerId;
    private String accountNo;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;
}
