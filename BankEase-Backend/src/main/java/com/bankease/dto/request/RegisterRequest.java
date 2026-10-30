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
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+0-9\\s-]{10,20}$", message = "Please provide a valid phone number")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    @Builder.Default
    private String accountType = "SAVINGS"; // SAVINGS, CURRENT, SALARY

    @DecimalMin(value = "0.0", inclusive = true, message = "Initial deposit cannot be negative")
    @Builder.Default
    private BigDecimal initialDeposit = BigDecimal.valueOf(1000.00);
}

