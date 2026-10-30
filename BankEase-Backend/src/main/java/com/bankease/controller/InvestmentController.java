package com.bankease.controller;

import com.bankease.dto.request.InvestmentRequest;
import com.bankease.dto.response.ApiResponse;
import com.bankease.dto.response.InvestmentDto;
import com.bankease.security.SecurityUtils;
import com.bankease.service.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getInvestmentPlans() {
        return ResponseEntity.ok(ApiResponse.success(investmentService.getInvestmentPlans()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InvestmentDto>> createInvestment(@Valid @RequestBody InvestmentRequest request) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        InvestmentDto inv = investmentService.applyInvestment(customerId, request);
        return ResponseEntity.ok(ApiResponse.success("Investment activated successfully", inv));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InvestmentDto>>> getMyInvestments(Pageable pageable) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        Page<InvestmentDto> investments = investmentService.getCustomerInvestments(customerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(investments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvestmentDto>> getInvestmentDetails(@PathVariable("id") Long investmentId) {
        InvestmentDto inv = investmentService.getInvestmentById(investmentId);
        return ResponseEntity.ok(ApiResponse.success(inv));
    }
}

