package com.bankease.service.impl;

import com.bankease.dto.request.LoanApplicationRequest;
import com.bankease.dto.request.StatusUpdateRequest;
import com.bankease.dto.response.LoanDto;
import com.bankease.entity.Customer;
import com.bankease.entity.Loan;
import com.bankease.exception.BadRequestException;
import com.bankease.exception.ResourceNotFoundException;
import com.bankease.repository.CustomerRepository;
import com.bankease.repository.LoanRepository;
import com.bankease.service.AuditService;
import com.bankease.service.LoanService;
import com.bankease.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Override
    @Transactional
    public LoanDto applyForLoan(Long customerId, LoanApplicationRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        BigDecimal interestRate = getInterestRateForType(request.getLoanType());
        BigDecimal emi = calculateEmi(request.getAmount(), interestRate, request.getTenureMonths());

        Loan loan = Loan.builder()
                .customer(customer)
                .loanType(request.getLoanType().toUpperCase())
                .amount(request.getAmount())
                .interestRate(interestRate)
                .tenureMonths(request.getTenureMonths())
                .monthlyEmi(emi)
                .status("SUBMITTED")
                .remarks(request.getRemarks())
                .build();

        Loan saved = loanRepository.save(loan);

        notificationService.createNotification(
                customerId,
                "Loan Application Submitted",
                "Your application for a " + loan.getLoanType() + " loan of Rs. " + loan.getAmount() + " (EMI: Rs. " + emi + ") has been received and is pending verification.",
                "LOAN"
        );

        auditService.log("LOAN_APPLICATION", "/api/loans/apply", "Submitted " + loan.getLoanType() + " loan for customer ID " + customerId);

        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDto> getCustomerLoans(Long customerId) {
        return loanRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanDto> getCustomerLoans(Long customerId, Pageable pageable) {
        return loanRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanDto getLoanById(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));
        return mapToDto(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDto> getAllLoans() {
        return loanRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanDto> getAllLoans(Pageable pageable) {
        return loanRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public LoanDto updateLoanStatus(Long loanId, StatusUpdateRequest request) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));

        String newStatus = request.getStatus().toUpperCase();
        loan.setStatus(newStatus);
        if (request.getRemarks() != null && !request.getRemarks().isBlank()) {
            loan.setRemarks(request.getRemarks());
        }

        Loan saved = loanRepository.save(loan);

        notificationService.createNotification(
                loan.getCustomer().getCustomerId(),
                "Loan Status Updated: " + newStatus,
                "Your " + loan.getLoanType() + " loan application for Rs. " + loan.getAmount() + " status is now: " + newStatus + ". Remarks: " + (loan.getRemarks() != null ? loan.getRemarks() : "None"),
                "LOAN"
        );

        auditService.log("LOAN_STATUS_UPDATE", "/api/loans/" + loanId + "/status", "Updated loan ID " + loanId + " to status " + newStatus);

        return mapToDto(saved);
    }

    @Override
    public List<Map<String, Object>> getLoanTypes() {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> home = new HashMap<>();
        home.put("type", "HOME");
        home.put("name", "Home Loan");
        home.put("interestRate", 8.50);
        home.put("minAmount", 500000.00);
        home.put("maxAmount", 10000000.00);
        home.put("maxTenureMonths", 360);
        home.put("description", "Financing your dream home with low floating interest rates and zero pre-payment charges.");
        list.add(home);

        Map<String, Object> personal = new HashMap<>();
        personal.put("type", "PERSONAL");
        personal.put("name", "Personal Loan");
        personal.put("interestRate", 11.25);
        personal.put("minAmount", 50000.00);
        personal.put("maxAmount", 1500000.00);
        personal.put("maxTenureMonths", 60);
        personal.put("description", "Quick disbursal instant personal loans for emergencies, travel, and personal milestones.");
        list.add(personal);

        Map<String, Object> education = new HashMap<>();
        education.put("type", "EDUCATION");
        education.put("name", "Education Loan");
        education.put("interestRate", 9.00);
        education.put("minAmount", 100000.00);
        education.put("maxAmount", 4000000.00);
        education.put("maxTenureMonths", 120);
        education.put("description", "Comprehensive student loans covering tuition fees, accommodation, and study supplies globally.");
        list.add(education);

        Map<String, Object> vehicle = new HashMap<>();
        vehicle.put("type", "VEHICLE");
        vehicle.put("name", "Vehicle Loan");
        vehicle.put("interestRate", 9.50);
        vehicle.put("minAmount", 100000.00);
        vehicle.put("maxAmount", 3000000.00);
        vehicle.put("maxTenureMonths", 84);
        vehicle.put("description", "Drive home your favorite two-wheeler or four-wheeler with flexible repayment options.");
        list.add(vehicle);

        return list;
    }

    private BigDecimal getInterestRateForType(String type) {
        if (type == null) return BigDecimal.valueOf(10.00);
        return switch (type.toUpperCase()) {
            case "HOME" -> BigDecimal.valueOf(8.50);
            case "PERSONAL" -> BigDecimal.valueOf(11.25);
            case "EDUCATION" -> BigDecimal.valueOf(9.00);
            case "VEHICLE" -> BigDecimal.valueOf(9.50);
            default -> BigDecimal.valueOf(10.00);
        };
    }

    private BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, int tenureMonths) {
        if (principal == null || tenureMonths <= 0) return BigDecimal.ZERO;
        double p = principal.doubleValue();
        double r = annualRate.doubleValue() / (12 * 100);
        double n = tenureMonths;

        double emi = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
    }

    private LoanDto mapToDto(Loan l) {
        return LoanDto.builder()
                .loanId(l.getLoanId())
                .customerId(l.getCustomer().getCustomerId())
                .customerName(l.getCustomer().getName())
                .loanType(l.getLoanType())
                .amount(l.getAmount())
                .interestRate(l.getInterestRate())
                .tenureMonths(l.getTenureMonths())
                .monthlyEmi(l.getMonthlyEmi())
                .status(l.getStatus())
                .remarks(l.getRemarks())
                .createdAt(l.getCreatedAt())
                .updatedAt(l.getUpdatedAt())
                .build();
    }
}

