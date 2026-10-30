package com.bankease.service.impl;

import com.bankease.dto.request.BillPaymentRequest;
import com.bankease.dto.response.BillPaymentDto;
import com.bankease.entity.Account;
import com.bankease.entity.BillPayment;
import com.bankease.entity.Customer;
import com.bankease.entity.Transaction;
import com.bankease.exception.BadRequestException;
import com.bankease.exception.InsufficientBalanceException;
import com.bankease.exception.ResourceNotFoundException;
import com.bankease.exception.UnauthorizedAccessException;
import com.bankease.repository.AccountRepository;
import com.bankease.repository.BillPaymentRepository;
import com.bankease.repository.CustomerRepository;
import com.bankease.repository.TransactionRepository;
import com.bankease.service.AuditService;
import com.bankease.service.BillPaymentService;
import com.bankease.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillPaymentServiceImpl implements BillPaymentService {

    private final BillPaymentRepository billPaymentRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;

    @Override
    @Transactional
    public BillPaymentDto payBill(Long customerId, BillPaymentRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Account account = accountRepository.findByAccountIdWithLock(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + request.getAccountId()));

        if (!account.getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Not authorized: Account does not belong to this customer");
        }

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new BadRequestException("Account is not active for bill payment transactions");
        }

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Bill amount must be positive");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient account balance. Available: Rs. " + account.getBalance() + ", Requested: Rs. " + amount);
        }

        // Debit account
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        String referenceNo = "BILL-BEAS-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        // Transaction record
        Transaction txn = Transaction.builder()
                .account(account)
                .type("DEBIT")
                .amount(amount)
                .balanceAfter(newBalance)
                .status("COMPLETED")
                .description("Bill Payment - " + request.getCategory() + " (" + request.getBillerName() + ")")
                .referenceNo("TXN-" + referenceNo)
                .build();
        transactionRepository.save(txn);

        // Bill Payment record
        BillPayment bill = BillPayment.builder()
                .account(account)
                .customer(customer)
                .category(request.getCategory().toUpperCase())
                .billerName(request.getBillerName())
                .consumerNumber(request.getConsumerNumber())
                .amount(amount)
                .status("PAID")
                .referenceNo(referenceNo)
                .build();
        BillPayment saved = billPaymentRepository.save(bill);

        // Notification
        notificationService.createNotification(
                customerId,
                "Bill Payment Successful",
                request.getCategory() + " bill payment of Rs. " + amount + " to " + request.getBillerName() + " (Consumer ID: " + request.getConsumerNumber() + ") paid successfully. Ref: " + referenceNo,
                "TRANSACTION"
        );

        auditService.log(
                "BILL_PAYMENT",
                "/api/bills/pay",
                "Paid " + request.getCategory() + " bill of Rs. " + amount + " to " + request.getBillerName() + " [Ref: " + referenceNo + "]"
        );

        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BillPaymentDto getBillPaymentById(Long customerId, Long billId) {
        BillPayment bill = billPaymentRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill payment not found with id: " + billId));

        if (!bill.getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Not authorized to view this bill payment record");
        }

        return mapToDto(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillPaymentDto> getBillHistory(Long customerId) {
        return billPaymentRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BillPaymentDto> getBillHistory(Long customerId, Pageable pageable) {
        return billPaymentRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId, pageable)
                .map(this::mapToDto);
    }

    @Override
    public List<String> getSupportedCategories() {
        return Arrays.asList("ELECTRICITY", "WATER", "MOBILE_RECHARGE", "INTERNET", "GAS");
    }

    private BillPaymentDto mapToDto(BillPayment b) {
        return BillPaymentDto.builder()
                .billId(b.getBillId())
                .accountId(b.getAccount().getAccountId())
                .accountNo(b.getAccount().getAccountNo())
                .category(b.getCategory())
                .billerName(b.getBillerName())
                .consumerNumber(b.getConsumerNumber())
                .amount(b.getAmount())
                .status(b.getStatus())
                .referenceNo(b.getReferenceNo())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
