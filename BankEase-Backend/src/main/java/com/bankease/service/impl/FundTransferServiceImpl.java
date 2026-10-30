package com.bankease.service.impl;

import com.bankease.dto.request.TransferRequest;
import com.bankease.dto.response.TransferResponse;
import com.bankease.entity.*;
import com.bankease.exception.BadRequestException;
import com.bankease.exception.InsufficientBalanceException;
import com.bankease.exception.ResourceNotFoundException;
import com.bankease.exception.UnauthorizedAccessException;
import com.bankease.repository.*;
import com.bankease.service.AuditService;
import com.bankease.service.FundTransferService;
import com.bankease.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FundTransferServiceImpl implements FundTransferService {

    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final TransferRepository transferRepository;
    private final TransactionRepository transactionRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public String generateOtpForTransfer(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Generate 6-digit OTP
        int randomPin = ThreadLocalRandom.current().nextInt(100000, 999999);
        String otpCode = String.valueOf(randomPin);

        OtpVerification otp = OtpVerification.builder()
                .user(user)
                .otpCode(otpCode)
                .purpose("TRANSFER")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();

        otpVerificationRepository.save(otp);
        return otpCode;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransferResponse processTransfer(Long customerId, Long userId, TransferRequest request) {
        // 1. Idempotency check to prevent duplicate transfers
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            Optional<IdempotencyRecord> existing = idempotencyRecordRepository.findById(request.getIdempotencyKey());
            if (existing.isPresent()) {
                try {
                    return objectMapper.readValue(existing.get().getResponsePayload(), TransferResponse.class);
                } catch (Exception ignored) {
                }
            }
        }

        // 2. Validate OTP verification (Accepts valid database OTP or universal test OTP 123456)
        validateOtp(userId, request.getOtpCode());

        // 3. Locking source account to prevent race conditions and concurrent double-spending
        Account sourceAccount = accountRepository.findByAccountIdWithLock(request.getSourceAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found with id: " + request.getSourceAccountId()));

        // 4. Verify account ownership
        if (!sourceAccount.getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Unauthorized: You do not own the source account");
        }

        // 5. Account status validation
        if (!"ACTIVE".equalsIgnoreCase(sourceAccount.getStatus())) {
            throw new BadRequestException("Source account is not active. Current status: " + sourceAccount.getStatus());
        }

        if (sourceAccount.getAccountNo().equals(request.getDestinationAccountNo())) {
            throw new BadRequestException("Cannot transfer funds to the same account");
        }

        BigDecimal transferAmount = request.getAmount();
        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transfer amount must be greater than zero");
        }

        // 6. Balance check
        if (sourceAccount.getBalance().compareTo(transferAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient funds. Available balance: Rs. " + sourceAccount.getBalance() + ", Requested: Rs. " + transferAmount);
        }

        // 7. Atomic Debit from Source Account
        BigDecimal sourceNewBalance = sourceAccount.getBalance().subtract(transferAmount);
        sourceAccount.setBalance(sourceNewBalance);
        accountRepository.save(sourceAccount);

        String referenceNo = "TRF-BEAS-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Transaction debitTxn = Transaction.builder()
                .account(sourceAccount)
                .type("DEBIT")
                .amount(transferAmount)
                .balanceAfter(sourceNewBalance)
                .status("COMPLETED")
                .description("Transfer to " + request.getDestinationAccountNo() + (request.getRemarks() != null ? " - " + request.getRemarks() : ""))
                .referenceNo("TXN-D-" + referenceNo)
                .build();
        transactionRepository.save(debitTxn);

        // 8. If destination account is internal to BankEase, credit atomically
        Optional<Account> destAccountOpt = accountRepository.findByAccountNoWithLock(request.getDestinationAccountNo());
        if (destAccountOpt.isPresent()) {
            Account destAccount = destAccountOpt.get();
            BigDecimal destNewBalance = destAccount.getBalance().add(transferAmount);
            destAccount.setBalance(destNewBalance);
            accountRepository.save(destAccount);

            Transaction creditTxn = Transaction.builder()
                    .account(destAccount)
                    .type("CREDIT")
                    .amount(transferAmount)
                    .balanceAfter(destNewBalance)
                    .status("COMPLETED")
                    .description("Transfer received from " + sourceAccount.getAccountNo())
                    .referenceNo("TXN-C-" + referenceNo)
                    .build();
            transactionRepository.save(creditTxn);

            // Notify destination customer
            notificationService.createNotification(
                    destAccount.getCustomer().getCustomerId(),
                    "Funds Received",
                    "Received Rs. " + transferAmount + " in account " + destAccount.getAccountNo() + " from " + sourceAccount.getAccountNo() + ". Ref: " + referenceNo,
                    "TRANSACTION"
            );
        }

        // 9. Save Beneficiary reference if linked
        Beneficiary beneficiary = null;
        if (request.getBeneficiaryId() != null) {
            beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId()).orElse(null);
        }

        // 10. Record Transfer
        Transfer transfer = Transfer.builder()
                .sourceAccount(sourceAccount)
                .destinationAccountNo(request.getDestinationAccountNo())
                .beneficiary(beneficiary)
                .amount(transferAmount)
                .status("COMPLETED")
                .referenceNo(referenceNo)
                .remarks(request.getRemarks())
                .build();
        Transfer savedTransfer = transferRepository.save(transfer);

        // 11. Customer Notification
        notificationService.createNotification(
                customerId,
                "Transfer Successful",
                "Successfully transferred Rs. " + transferAmount + " to " + request.getDestinationAccountNo() + ". Ref: " + referenceNo,
                "TRANSACTION"
        );

        // 12. Audit Log
        auditService.log(
                "FUND_TRANSFER",
                "/api/transfers",
                "Transferred Rs. " + transferAmount + " from " + sourceAccount.getAccountNo() + " to " + request.getDestinationAccountNo() + " [Ref: " + referenceNo + "]"
        );

        TransferResponse response = TransferResponse.builder()
                .transferId(savedTransfer.getTransferId())
                .referenceNo(savedTransfer.getReferenceNo())
                .sourceAccountNo(sourceAccount.getAccountNo())
                .destinationAccountNo(request.getDestinationAccountNo())
                .amount(transferAmount)
                .status(savedTransfer.getStatus())
                .remarks(savedTransfer.getRemarks())
                .timestamp(savedTransfer.getCreatedAt())
                .build();

        // 13. Cache for Idempotency
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            try {
                String payload = objectMapper.writeValueAsString(response);
                idempotencyRecordRepository.save(new IdempotencyRecord(request.getIdempotencyKey(), payload, LocalDateTime.now()));
            } catch (Exception ignored) {
            }
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public TransferResponse getTransferById(Long customerId, Long transferId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with id: " + transferId));

        if (!transfer.getSourceAccount().getCustomer().getCustomerId().equals(customerId)) {
            throw new UnauthorizedAccessException("Not authorized to view this transfer record");
        }

        return mapToDto(transfer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResponse> getTransferHistory(Long customerId) {
        return transferRepository.findBySourceAccountCustomerCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferResponse> getTransferHistory(Long customerId, Pageable pageable) {
        return transferRepository.findBySourceAccountCustomerCustomerIdOrderByCreatedAtDesc(customerId, pageable)
                .map(this::mapToDto);
    }

    private void validateOtp(Long userId, String otpCode) {
        if (otpCode == null || otpCode.trim().isEmpty()) {
            throw new BadRequestException("OTP code is required to authorize transfer");
        }
        // Universal demo OTP or valid database OTP
        if ("123456".equals(otpCode.trim())) {
            return;
        }

        Optional<OtpVerification> opt = otpVerificationRepository
                .findTopByUserUserIdAndOtpCodeAndPurposeAndVerifiedFalseAndExpiresAtAfter(
                        userId, otpCode.trim(), "TRANSFER", LocalDateTime.now()
                );

        if (opt.isEmpty()) {
            throw new BadRequestException("Invalid or expired OTP. Please request a new OTP code.");
        }

        OtpVerification otp = opt.get();
        otp.setVerified(true);
        otpVerificationRepository.save(otp);
    }

    private TransferResponse mapToDto(Transfer t) {
        return TransferResponse.builder()
                .transferId(t.getTransferId())
                .referenceNo(t.getReferenceNo())
                .sourceAccountNo(t.getSourceAccount().getAccountNo())
                .destinationAccountNo(t.getDestinationAccountNo())
                .amount(t.getAmount())
                .status(t.getStatus())
                .remarks(t.getRemarks())
                .timestamp(t.getCreatedAt())
                .build();
    }
}

