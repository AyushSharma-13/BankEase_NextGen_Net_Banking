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
public class LoanApplicationRequest {

    @NotBlank(message = "Loan type is required")
    private String loanType; // HOME, PERSONAL, EDUCATION, VEHICLE

    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "5000.00", message = "Minimum loan amount is 5000.00")
    private BigDecimal amount;

    @NotNull(message = "Tenure in months is required")
    @Min(value = 6, message = "Minimum tenure is 6 months")
    @Max(value = 360, message = "Maximum tenure is 360 months")
    private Integer tenureMonths;

    private String remarks;
}
