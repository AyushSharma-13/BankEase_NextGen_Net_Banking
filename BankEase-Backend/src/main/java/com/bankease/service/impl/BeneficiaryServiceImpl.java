package com.bankease.service.impl;

import com.bankease.dto.request.BeneficiaryRequest;
import com.bankease.dto.response.BeneficiaryDto;
import com.bankease.entity.Beneficiary;
import com.bankease.entity.Customer;
import com.bankease.exception.DuplicateResourceException;
import com.bankease.exception.ResourceNotFoundException;
import com.bankease.exception.UnauthorizedAccessException;
import com.bankease.repository.AccountRepository;
import com.bankease.repository.BeneficiaryRepository;
import com.bankease.repository.CustomerRepository;
import com.bankease.service.BeneficiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BeneficiaryDto> getBeneficiaries(Long customerId) {
        return beneficiaryRepository.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BeneficiaryDto getBeneficiaryById(Long customerId, Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id: " + beneficiaryId));

        if (!beneficiary.getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Not authorized to view this beneficiary");
        }

        return mapToDto(beneficiary);
    }

    @Override
    @Transactional
    public BeneficiaryDto addBeneficiary(Long customerId, BeneficiaryRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        if (beneficiaryRepository.existsByCustomerCustomerIdAndAccountNo(customerId, request.getAccountNo())) {
            throw new DuplicateResourceException("Beneficiary with account number " + request.getAccountNo() + " already exists in your beneficiary list");
        }

        Beneficiary beneficiary = Beneficiary.builder()
                .customer(customer)
                .name(request.getName())
                .accountNo(request.getAccountNo())
                .bankName(request.getBankName())
                .ifscCode(request.getIfscCode().toUpperCase())
                .status("ACTIVE")
                .build();

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public BeneficiaryDto updateBeneficiary(Long customerId, Long beneficiaryId, BeneficiaryRequest request) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id: " + beneficiaryId));

        if (!beneficiary.getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Not authorized to update this beneficiary");
        }

        beneficiary.setName(request.getName());
        beneficiary.setAccountNo(request.getAccountNo());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setIfscCode(request.getIfscCode().toUpperCase());

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteBeneficiary(Long customerId, Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found with id: " + beneficiaryId));

        if (!beneficiary.getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Not authorized to delete this beneficiary");
        }

        beneficiaryRepository.delete(beneficiary);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateAccount(String accountNo) {
        // Checks whether account exists in BankEase internal accounts or has valid 9-18 digit format
        if (accountNo == null || !accountNo.matches("^[0-9]{9,18}$")) {
            return false;
        }
        return true;
    }

    private BeneficiaryDto mapToDto(Beneficiary b) {
        return BeneficiaryDto.builder()
                .beneficiaryId(b.getBeneficiaryId())
                .customerId(b.getCustomer().getCustomerId())
                .name(b.getName())
                .accountNo(b.getAccountNo())
                .bankName(b.getBankName())
                .ifscCode(b.getIfscCode())
                .status(b.getStatus())
                .createdAt(b.getCreatedAt())
                .build();
    }
}

