package com.bankease.controller;

import com.bankease.dto.request.BeneficiaryRequest;
import com.bankease.dto.response.ApiResponse;
import com.bankease.dto.response.BeneficiaryDto;
import com.bankease.security.SecurityUtils;
import com.bankease.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BeneficiaryDto>>> listBeneficiaries() {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        List<BeneficiaryDto> list = beneficiaryService.getBeneficiaries(customerId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BeneficiaryDto>> getBeneficiary(@PathVariable("id") Long beneficiaryId) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        BeneficiaryDto beneficiary = beneficiaryService.getBeneficiaryById(customerId, beneficiaryId);
        return ResponseEntity.ok(ApiResponse.success(beneficiary));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BeneficiaryDto>> addBeneficiary(@Valid @RequestBody BeneficiaryRequest request) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        BeneficiaryDto created = beneficiaryService.addBeneficiary(customerId, request);
        return ResponseEntity.ok(ApiResponse.success("Beneficiary added successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BeneficiaryDto>> updateBeneficiary(
            @PathVariable("id") Long beneficiaryId,
            @Valid @RequestBody BeneficiaryRequest request) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        BeneficiaryDto updated = beneficiaryService.updateBeneficiary(customerId, beneficiaryId, request);
        return ResponseEntity.ok(ApiResponse.success("Beneficiary updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBeneficiary(@PathVariable("id") Long beneficiaryId) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        beneficiaryService.deleteBeneficiary(customerId, beneficiaryId);
        return ResponseEntity.ok(ApiResponse.success("Beneficiary removed successfully", null));
    }

    @GetMapping("/validate/{accountNo}")
    public ResponseEntity<ApiResponse<Boolean>> validateAccount(@PathVariable("accountNo") String accountNo) {
        boolean isValid = beneficiaryService.validateAccount(accountNo);
        return ResponseEntity.ok(ApiResponse.success(isValid));
    }
}
