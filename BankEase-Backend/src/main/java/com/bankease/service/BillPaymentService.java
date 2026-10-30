package com.bankease.service;

import com.bankease.dto.request.BillPaymentRequest;
import com.bankease.dto.response.BillPaymentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillPaymentService {
    BillPaymentDto payBill(Long customerId, BillPaymentRequest request);
    BillPaymentDto getBillPaymentById(Long customerId, Long billId);
    List<BillPaymentDto> getBillHistory(Long customerId);
    Page<BillPaymentDto> getBillHistory(Long customerId, Pageable pageable);
    List<String> getSupportedCategories();
}
