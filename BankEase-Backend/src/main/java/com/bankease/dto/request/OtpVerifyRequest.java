package com.bankease.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequest {
    @NotBlank(message = "OTP code is required")
    private String otpCode;

    @NotBlank(message = "Purpose is required")
    private String purpose;
}
