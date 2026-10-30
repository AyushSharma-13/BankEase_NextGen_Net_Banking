package com.bankease.service;

import com.bankease.dto.request.TransferRequest;
import com.bankease.dto.response.TransferResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FundTransferService {
    String generateOtpForTransfer(Long userId);
    TransferResponse processTransfer(Long customerId, Long userId, TransferRequest request);
    TransferResponse getTransferById(Long customerId, Long transferId);
    List<TransferResponse> getTransferHistory(Long customerId);
    Page<TransferResponse> getTransferHistory(Long customerId, Pageable pageable);
}

