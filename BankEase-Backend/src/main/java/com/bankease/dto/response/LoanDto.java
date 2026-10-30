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
public class LoanDto {
    private Long loanId;
    private Long customerId;
    private String customerName;
    private String loanType;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal monthlyEmi;
    private String status;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
