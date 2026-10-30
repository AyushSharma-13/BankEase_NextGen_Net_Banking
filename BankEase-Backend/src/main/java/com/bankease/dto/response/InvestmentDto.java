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
public class InvestmentDto {
    private Long investmentId;
    private Long customerId;
    private String customerName;
    private String investmentType;
    private BigDecimal amount;
    private Integer tenureMonths;
    private BigDecimal expectedReturnRate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
