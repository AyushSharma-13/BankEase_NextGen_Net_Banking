package com.bankease.service;

import com.bankease.dto.request.LoanApplicationRequest;
import com.bankease.dto.request.StatusUpdateRequest;
import com.bankease.dto.response.LoanDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface LoanService {
    LoanDto applyForLoan(Long customerId, LoanApplicationRequest request);
    List<LoanDto> getCustomerLoans(Long customerId);
    Page<LoanDto> getCustomerLoans(Long customerId, Pageable pageable);
    LoanDto getLoanById(Long loanId);
    List<LoanDto> getAllLoans();
    Page<LoanDto> getAllLoans(Pageable pageable);
    LoanDto updateLoanStatus(Long loanId, StatusUpdateRequest request);
    List<Map<String, Object>> getLoanTypes();
}
