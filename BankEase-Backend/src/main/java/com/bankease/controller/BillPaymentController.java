package com.bankease.controller;

import com.bankease.dto.request.BillPaymentRequest;
import com.bankease.dto.response.ApiResponse;
import com.bankease.dto.response.BillPaymentDto;
import com.bankease.security.SecurityUtils;
import com.bankease.service.BillPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillPaymentController {

    private final BillPaymentService billPaymentService;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(billPaymentService.getSupportedCategories()));
    }

    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<BillPaymentDto>> payBill(@Valid @RequestBody BillPaymentRequest request) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        BillPaymentDto result = billPaymentService.payBill(customerId, request);
        return ResponseEntity.ok(ApiResponse.success("Bill payment processed successfully", result));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<BillPaymentDto>>> getBillHistory(Pageable pageable) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        Page<BillPaymentDto> history = billPaymentService.getBillHistory(customerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BillPaymentDto>> getBillPayment(@PathVariable("id") Long billId) {
        Long customerId = SecurityUtils.getCurrentCustomerId();
        BillPaymentDto bill = billPaymentService.getBillPaymentById(customerId, billId);
        return ResponseEntity.ok(ApiResponse.success(bill));
    }
}
