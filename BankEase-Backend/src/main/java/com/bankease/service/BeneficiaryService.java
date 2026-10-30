package com.bankease.service;

import com.bankease.dto.request.BeneficiaryRequest;
import com.bankease.dto.response.BeneficiaryDto;

import java.util.List;

public interface BeneficiaryService {
    List<BeneficiaryDto> getBeneficiaries(Long customerId);
    BeneficiaryDto getBeneficiaryById(Long customerId, Long beneficiaryId);
    BeneficiaryDto addBeneficiary(Long customerId, BeneficiaryRequest request);
    BeneficiaryDto updateBeneficiary(Long customerId, Long beneficiaryId, BeneficiaryRequest request);
    void deleteBeneficiary(Long customerId, Long beneficiaryId);
    boolean validateAccount(String accountNo);
}

