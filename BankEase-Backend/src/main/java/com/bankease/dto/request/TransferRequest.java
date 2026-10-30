package com.bankease.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotNull(message = "Source account ID is required")
    private Long sourceAccountId;

    @NotBlank(message = "Destination account number is required")
    private String destinationAccountNo;

    private Long beneficiaryId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum transfer amount is 1.00")
    private BigDecimal amount;

    private String remarks;

    @NotBlank(message = "OTP verification code is required")
    private String otpCode;

    private String idempotencyKey;
}
