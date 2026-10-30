package com.bankease.controller;

import com.bankease.dto.request.StatusUpdateRequest;
import com.bankease.dto.response.ApiResponse;
import com.bankease.dto.response.InvestmentDto;
import com.bankease.dto.response.LoanDto;
import com.bankease.service.InvestmentService;
import com.bankease.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasAnyRole('BANK_STAFF', 'ADMIN')")
@RequiredArgsConstructor
public class StaffController {

    private final LoanService loanService;
    private final InvestmentService investmentService;

    @GetMapping("/loans")
    public ResponseEntity<ApiResponse<Page<LoanDto>>> getAllLoans(Pageable pageable) {
        Page<LoanDto> loans = loanService.getAllLoans(pageable);
        return ResponseEntity.ok(ApiResponse.success(loans));
    }

    @PatchMapping("/loans/{id}/status")
    public ResponseEntity<ApiResponse<LoanDto>> updateLoanStatus(
            @PathVariable("id") Long loanId,
            @Valid @RequestBody StatusUpdateRequest request) {
        LoanDto updated = loanService.updateLoanStatus(loanId, request);
        return ResponseEntity.ok(ApiResponse.success("Loan status updated successfully", updated));
    }

    @GetMapping("/investments")
    public ResponseEntity<ApiResponse<Page<InvestmentDto>>> getAllInvestments(Pageable pageable) {
        Page<InvestmentDto> investments = investmentService.getAllInvestments(pageable);
        return ResponseEntity.ok(ApiResponse.success(investments));
    }

    @PatchMapping("/investments/{id}/status")
    public ResponseEntity<ApiResponse<InvestmentDto>> updateInvestmentStatus(
            @PathVariable("id") Long investmentId,
            @Valid @RequestBody StatusUpdateRequest request) {
        InvestmentDto updated = investmentService.updateInvestmentStatus(investmentId, request);
        return ResponseEntity.ok(ApiResponse.success("Investment status updated successfully", updated));
    }
}
