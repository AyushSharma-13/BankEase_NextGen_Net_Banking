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
public class BillPaymentDto {
    private Long billId;
    private Long accountId;
    private String accountNo;
    private String category;
    private String billerName;
    private String consumerNumber;
    private BigDecimal amount;
    private String status;
    private String referenceNo;
    private LocalDateTime createdAt;
}

