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
public class InvestmentRequest {

    @NotBlank(message = "Investment type is required")
    private String investmentType; // FIXED_DEPOSIT, RECURRING_DEPOSIT, MUTUAL_FUNDS, NATIONAL_PENSION_SCHEME

    @NotNull(message = "Investment amount is required")
    @DecimalMin(value = "500.00", message = "Minimum investment amount is 500.00")
    private BigDecimal amount;

    @NotNull(message = "Tenure in months is required")
    @Min(value = 3, message = "Minimum tenure is 3 months")
    @Max(value = 120, message = "Maximum tenure is 120 months")
    private Integer tenureMonths;
}

