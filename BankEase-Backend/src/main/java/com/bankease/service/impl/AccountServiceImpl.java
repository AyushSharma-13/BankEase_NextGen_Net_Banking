package com.bankease.service.impl;

import com.bankease.dto.response.AccountDto;
import com.bankease.dto.response.DashboardOverviewDto;
import com.bankease.dto.response.TransactionDto;
import com.bankease.entity.Account;
import com.bankease.entity.Customer;
import com.bankease.exception.ResourceNotFoundException;
import com.bankease.exception.UnauthorizedAccessException;
import com.bankease.repository.*;
import com.bankease.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final LoanRepository loanRepository;
    private final InvestmentRepository investmentRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long accountId, Long customerId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        if (!account.getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("You are not authorized to view this account");
        }

        return mapToDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountByAccountNo(String accountNo) {
        Account account = accountRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNo));
        return mapToDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardOverviewDto getDashboardOverview(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        List<AccountDto> accounts = getAccountsByCustomerId(customerId);

        BigDecimal totalBalance = accounts.stream()
                .map(AccountDto::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TransactionDto> recentTxns = transactionRepository.findTop10ByAccountCustomerCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(t -> TransactionDto.builder()
                        .transactionId(t.getTransactionId())
                        .accountId(t.getAccount().getAccountId())
                        .accountNo(t.getAccount().getAccountNo())
                        .type(t.getType())
                        .amount(t.getAmount())
                        .balanceAfter(t.getBalanceAfter())
                        .status(t.getStatus())
                        .description(t.getDescription())
                        .referenceNo(t.getReferenceNo())
                        .createdAt(t.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        long unreadNotifs = notificationRepository.countByCustomerCustomerIdAndIsReadFalse(customerId);
        long beneficiariesCount = beneficiaryRepository.findByCustomerCustomerId(customerId).size();
        long loansCount = loanRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId).size();
        long investmentsCount = investmentRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customerId).size();

        return DashboardOverviewDto.builder()
                .customerName(customer.getName())
                .email(customer.getEmail())
                .accounts(accounts)
                .totalBalance(totalBalance)
                .recentTransactions(recentTxns)
                .unreadNotificationsCount(unreadNotifs)
                .totalBeneficiariesCount(beneficiariesCount)
                .activeLoansCount(loansCount)
                .activeInvestmentsCount(investmentsCount)
                .build();
    }

    private AccountDto mapToDto(Account a) {
        return AccountDto.builder()
                .accountId(a.getAccountId())
                .customerId(a.getCustomer().getCustomerId())
                .accountNo(a.getAccountNo())
                .accountType(a.getAccountType())
                .balance(a.getBalance())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .build();
    }
}

