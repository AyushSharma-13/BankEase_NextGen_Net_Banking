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
public class BillPaymentRequest {

    @NotNull(message = "Source account ID is required")
    private Long accountId;

    @NotBlank(message = "Category is required")
    private String category; // ELECTRICITY, WATER, MOBILE_RECHARGE, INTERNET, GAS

    @NotBlank(message = "Biller name is required")
    private String billerName;

    @NotBlank(message = "Consumer/Account number is required")
    private String consumerNumber;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "1.00", message = "Minimum payment amount is 1.00")
    private BigDecimal amount;
}
