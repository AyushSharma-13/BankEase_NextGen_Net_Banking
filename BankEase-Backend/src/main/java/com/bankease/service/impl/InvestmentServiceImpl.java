package com.bankease.service.impl;

import com.bankease.dto.request.InvestmentRequest;
import com.bankease.dto.request.StatusUpdateRequest;
import com.bankease.dto.response.InvestmentDto;
import com.bankease.entity.Customer;
import com.bankease.entity.Investment;
import com.bankease.exception.ResourceNotFoundException;
import com.bankease.repository.CustomerRepository;
import com.bankease.repository.InvestmentRepository;
import com.bankease.service.AuditService;
import com.bankease.service.InvestmentService;
import com.bankease.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final CustomerRepository customerRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Override
    @Transactional
    public InvestmentDto applyInvestment(Long customerId, InvestmentRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        BigDecimal returnRate = getExpectedReturnRate(request.getInvestmentType());

        Investment investment = Investment.builder()
                .customer(customer)
                .investmentType(request.getInvestmentType().toUpperCase())
                .amount(request.getAmount())
                .tenureMonths(request.getTenureMonths())
                .expectedReturnRate(returnRate)
                .status("ACTIVE")
                .build();

        Investment saved = investmentRepository.save(investment);

        notificationService.createNotification(
                customerId,
                "Investment Portfolio Created",
                "Your investment in " + investment.getInvestmentType() + " for Rs. " + investment.getAmount() + " with expected return of " + returnRate + "% p.a. has been activated.",
                "INVESTMENT"
        );

        auditService.log("INVESTMENT_CREATE", "/api/investments", "Created investment of Rs. " + investment.getAmount() + " for customer ID " + customerId);

        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentDto> getCustomerInvestments(Long customerId) {
        return investmentRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvestmentDto> getCustomerInvestments(Long customerId, Pageable pageable) {
        return investmentRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public InvestmentDto getInvestmentById(Long investmentId) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + investmentId));
        return mapToDto(investment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentDto> getAllInvestments() {
        return investmentRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvestmentDto> getAllInvestments(Pageable pageable) {
        return investmentRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public InvestmentDto updateInvestmentStatus(Long investmentId, StatusUpdateRequest request) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + investmentId));

        String newStatus = request.getStatus().toUpperCase();
        investment.setStatus(newStatus);
        Investment saved = investmentRepository.save(investment);

        notificationService.createNotification(
                investment.getCustomer().getCustomerId(),
                "Investment Status Updated",
                "Your " + investment.getInvestmentType() + " investment status has been updated to: " + newStatus,
                "INVESTMENT"
        );

        auditService.log("INVESTMENT_STATUS_UPDATE", "/api/investments/" + investmentId + "/status", "Updated investment ID " + investmentId + " to " + newStatus);

        return mapToDto(saved);
    }

    @Override
    public List<Map<String, Object>> getInvestmentPlans() {
        List<Map<String, Object>> plans = new ArrayList<>();

        Map<String, Object> fd = new HashMap<>();
        fd.put("type", "FIXED_DEPOSIT");
        fd.put("name", "Fixed Deposit (FD)");
        fd.put("returnRate", 7.10);
        fd.put("minAmount", 5000.00);
        fd.put("riskLevel", "Zero Risk");
        fd.put("description", "Guaranteed interest returns backed by sovereign bank safety with premature withdrawal options.");
        plans.add(fd);

        Map<String, Object> rd = new HashMap<>();
        rd.put("type", "RECURRING_DEPOSIT");
        rd.put("name", "Recurring Deposit (RD)");
        rd.put("returnRate", 6.85);
        rd.put("minAmount", 1000.00);
        rd.put("riskLevel", "Zero Risk");
        rd.put("description", "Discipline your savings every month with attractive compounded quarterly interest yields.");
        plans.add(rd);

        Map<String, Object> mf = new HashMap<>();
        mf.put("type", "MUTUAL_FUNDS");
        mf.put("name", "Equity & Hybrid Mutual Funds");
        mf.put("returnRate", 12.50);
        mf.put("minAmount", 500.00);
        mf.put("riskLevel", "Moderate to High");
        mf.put("description", "Professional asset management investing in diversified top blue-chip and high-growth equities.");
        plans.add(mf);

        Map<String, Object> nps = new HashMap<>();
        nps.put("type", "NATIONAL_PENSION_SCHEME");
        nps.put("name", "National Pension Scheme (NPS)");
        nps.put("returnRate", 9.75);
        nps.put("minAmount", 1000.00);
        nps.put("riskLevel", "Low to Moderate");
        nps.put("description", "Government-sponsored retirement pension investment with attractive tax exemption under Section 80CCD.");
        plans.add(nps);

        return plans;
    }

    private BigDecimal getExpectedReturnRate(String type) {
        if (type == null) return BigDecimal.valueOf(7.00);
        return switch (type.toUpperCase()) {
            case "FIXED_DEPOSIT" -> BigDecimal.valueOf(7.10);
            case "RECURRING_DEPOSIT" -> BigDecimal.valueOf(6.85);
            case "MUTUAL_FUNDS" -> BigDecimal.valueOf(12.50);
            case "NATIONAL_PENSION_SCHEME" -> BigDecimal.valueOf(9.75);
            default -> BigDecimal.valueOf(7.00);
        };
    }

    private InvestmentDto mapToDto(Investment inv) {
        return InvestmentDto.builder()
                .investmentId(inv.getInvestmentId())
                .customerId(inv.getCustomer().getCustomerId())
                .customerName(inv.getCustomer().getName())
                .investmentType(inv.getInvestmentType())
                .amount(inv.getAmount())
                .tenureMonths(inv.getTenureMonths())
                .expectedReturnRate(inv.getExpectedReturnRate())
                .status(inv.getStatus())
                .createdAt(inv.getCreatedAt())
                .updatedAt(inv.getUpdatedAt())
                .build();
    }
}
