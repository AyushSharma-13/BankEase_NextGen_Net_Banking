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
public class TransferResponse {
    private Long transferId;
    private String referenceNo;
    private String sourceAccountNo;
    private String destinationAccountNo;
    private BigDecimal amount;
    private String status;
    private String remarks;
    private LocalDateTime timestamp;
}
