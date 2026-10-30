package com.bankease.service;

import com.bankease.dto.request.InvestmentRequest;
import com.bankease.dto.request.StatusUpdateRequest;
import com.bankease.dto.response.InvestmentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface InvestmentService {
    InvestmentDto applyInvestment(Long customerId, InvestmentRequest request);
    List<InvestmentDto> getCustomerInvestments(Long customerId);
    Page<InvestmentDto> getCustomerInvestments(Long customerId, Pageable pageable);
    InvestmentDto getInvestmentById(Long investmentId);
    List<InvestmentDto> getAllInvestments();
    Page<InvestmentDto> getAllInvestments(Pageable pageable);
    InvestmentDto updateInvestmentStatus(Long investmentId, StatusUpdateRequest request);
    List<Map<String, Object>> getInvestmentPlans();
}

