package com.bankease.service.impl;

import com.bankease.dto.response.TransactionDto;
import com.bankease.entity.Account;
import com.bankease.entity.Transaction;
import com.bankease.exception.ResourceNotFoundException;
import com.bankease.exception.UnauthorizedAccessException;
import com.bankease.repository.AccountRepository;
import com.bankease.repository.TransactionRepository;
import com.bankease.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDto> getTransactions(Long customerId, String type, String status,
                                                LocalDateTime startDate, LocalDateTime endDate,
                                                String search, Pageable pageable) {
        String cleanType = (type != null && !type.isBlank()) ? type.toUpperCase() : null;
        String cleanStatus = (status != null && !status.isBlank()) ? status.toUpperCase() : null;
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;

        return transactionRepository.filterTransactions(customerId, cleanType, cleanStatus, startDate, endDate, cleanSearch, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDto> getAccountTransactions(Long customerId, Long accountId, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        if (!account.getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Not authorized to access transactions for this account");
        }

        return transactionRepository.findByAccountAccountIdOrderByCreatedAtDesc(accountId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDto getTransactionByReference(Long customerId, String referenceNo) {
        Transaction txn = transactionRepository.findByReferenceNo(referenceNo)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found for reference: " + referenceNo));

        if (!txn.getAccount().getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Not authorized to view this transaction");
        }

        return mapToDto(txn);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generateAccountStatement(Long customerId, Long accountId, LocalDateTime from, LocalDateTime to) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        if (!account.getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Not authorized to generate statement for this account");
        }

        List<Transaction> transactions = account.getTransactions();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                               BANKEASE DIGITAL NET BANKING                             \n");
        sb.append("                                OFFICIAL ACCOUNT STATEMENT                              \n");
        sb.append("========================================================================================\n");
        sb.append("Customer Name  : ").append(account.getCustomer().getName()).append("\n");
        sb.append("Account Number : ").append(account.getAccountNo()).append("\n");
        sb.append("Account Type   : ").append(account.getAccountType()).append("\n");
        sb.append("Current Balance: Rs. ").append(account.getBalance()).append("\n");
        sb.append("Generated On   : ").append(LocalDateTime.now().format(dtf)).append("\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-22s %-25s %-8s %-14s %-14s %s\n", "Date & Time", "Reference No", "Type", "Amount (Rs.)", "Balance (Rs.)", "Description"));
        sb.append("----------------------------------------------------------------------------------------\n");

        for (Transaction txn : transactions) {
            sb.append(String.format("%-22s %-25s %-8s %-14.2f %-14.2f %s\n",
                    txn.getCreatedAt() != null ? txn.getCreatedAt().format(dtf) : "N/A",
                    txn.getReferenceNo(),
                    txn.getType(),
                    txn.getAmount(),
                    txn.getBalanceAfter(),
                    txn.getDescription()
            ));
        }
        sb.append("========================================================================================\n");
        sb.append("                  * This is a computer generated simulated banking document *           \n");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private TransactionDto mapToDto(Transaction t) {
        return TransactionDto.builder()
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
                .build();
    }
}
