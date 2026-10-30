package com.bankease.controller;

import com.bankease.dto.request.TransferRequest;
import com.bankease.dto.response.ApiResponse;
import com.bankease.dto.response.TransferResponse;
import com.bankease.security.SecurityUtils;
import com.bankease.security.UserPrincipal;
import com.bankease.service.FundTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final FundTransferService fundTransferService;

    @PostMapping("/otp")
    public ResponseEntity<ApiResponse<Map<String, String>>> requestTransferOtp() {
        UserPrincipal user = SecurityUtils.getCurrentUser();
        String otpCode = fundTransferService.generateOtpForTransfer(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(
                "OTP sent to your registered mobile and email",
                Map.of("otpCode", otpCode, "expiresInMinutes", "5")
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransferResponse>> processTransfer(@Valid @RequestBody TransferRequest request) {
        UserPrincipal user = SecurityUtils.getCurrentUser();
        TransferResponse response = fundTransferService.processTransfer(user.getCustomerId(), user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Transfer completed successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransferResponse>> getTransfer(@PathVariable("id") Long transferId) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        TransferResponse response = fundTransferService.getTransferById(customerId, transferId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransferResponse>>> getTransferHistory(Pageable pageable) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        Page<TransferResponse> page = fundTransferService.getTransferHistory(customerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }
}
