package com.bankease.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryDto {
    private Long beneficiaryId;
    private Long customerId;
    private String name;
    private String accountNo;
    private String bankName;
    private String ifscCode;
    private String status;
    private LocalDateTime createdAt;
}
