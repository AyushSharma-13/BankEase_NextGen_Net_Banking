package com.bankease.controller;

import com.bankease.dto.request.LoanApplicationRequest;
import com.bankease.dto.response.ApiResponse;
import com.bankease.dto.response.LoanDto;
import com.bankease.security.SecurityUtils;
import com.bankease.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLoanTypes() {
        return ResponseEntity.ok(ApiResponse.success(loanService.getLoanTypes()));
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<LoanDto>> applyForLoan(@Valid @RequestBody LoanApplicationRequest request) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        LoanDto loan = loanService.applyForLoan(customerId, request);
        return ResponseEntity.ok(ApiResponse.success("Loan application submitted successfully", loan));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LoanDto>>> getMyLoans(Pageable pageable) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        Page<LoanDto> loans = loanService.getCustomerLoans(customerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(loans));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanDto>> getLoanDetails(@PathVariable("id") Long loanId) {
        LoanDto loan = loanService.getLoanById(loanId);
        return ResponseEntity.ok(ApiResponse.success(loan));
    }
}
