package com.bankease.service.impl;

import com.bankease.dto.response.AccountDto;
import com.bankease.dto.response.TransactionDto;
import com.bankease.dto.response.UserDto;
import com.bankease.entity.*;
import com.bankease.exception.DuplicateResourceException;
import com.bankease.exception.ResourceNotFoundException;
import com.bankease.repository.*;
import com.bankease.service.AdminService;
import com.bankease.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final InvestmentRepository investmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemMetrics() {
        long totalCustomers = customerRepository.count();
        long totalUsers = userRepository.count();
        long totalAccounts = accountRepository.count();
        long totalTransactions = transactionRepository.count();
        long totalLoans = loanRepository.count();
        long totalInvestments = investmentRepository.count();

        BigDecimal totalDeposits = accountRepository.findAll().stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", totalCustomers);
        stats.put("totalUsers", totalUsers);
        stats.put("totalAccounts", totalAccounts);
        stats.put("totalTransactions", totalTransactions);
        stats.put("totalDeposits", totalDeposits);
        stats.put("totalLoans", totalLoans);
        stats.put("totalInvestments", totalInvestments);
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapUserToDto);
    }

    @Override
    @Transactional
    public UserDto toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setEnabled(!Boolean.TRUE.equals(user.getEnabled()));
        User saved = userRepository.save(user);

        auditService.log("TOGGLE_USER_STATUS", "/api/admin/users/" + userId + "/toggle", "Changed enabled to " + saved.getEnabled() + " for user " + saved.getUsername());
        return mapUserToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountDto> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable).map(a -> AccountDto.builder()
                .accountId(a.getAccountId())
                .customerId(a.getCustomer().getCustomerId())
                .accountNo(a.getAccountNo())
                .accountType(a.getAccountType())
                .balance(a.getBalance())
                .status(a.getStatus())
                .createdAt(a.getCreatedAt())
                .build());
    }

    @Override
    @Transactional
    public AccountDto updateAccountStatus(Long accountId, String status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + accountId));

        account.setStatus(status.toUpperCase());
        Account saved = accountRepository.save(account);

        auditService.log("ACCOUNT_STATUS_CHANGE", "/api/admin/accounts/" + accountId + "/status", "Updated account status to " + status + " for " + account.getAccountNo());

        return AccountDto.builder()
                .accountId(saved.getAccountId())
                .customerId(saved.getCustomer().getCustomerId())
                .accountNo(saved.getAccountNo())
                .accountType(saved.getAccountType())
                .balance(saved.getBalance())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDto> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable).map(t -> TransactionDto.builder()
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
                .build());
    }

    @Override
    @Transactional
    public UserDto createStaffUser(String username, String password, String name, String email, String phone) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username '" + username + "' is already taken");
        }

        Customer customer = Customer.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .address("BankEase Staff Branch Operations")
                .build();
        Customer savedCustomer = customerRepository.save(customer);

        User staff = User.builder()
                .customer(savedCustomer)
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .role(Role.BANK_STAFF)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
        User savedStaff = userRepository.save(staff);

        auditService.log("CREATE_STAFF", "/api/admin/staff", "Created bank staff member: " + username);
        return mapUserToDto(savedStaff);
    }

    private UserDto mapUserToDto(User u) {
        Customer c = u.getCustomer();
        return UserDto.builder()
                .userId(u.getUserId())
                .customerId(c != null ? c.getCustomerId() : null)
                .username(u.getUsername())
                .role(u.getRole().name())
                .name(c != null ? c.getName() : u.getUsername())
                .email(c != null ? c.getEmail() : "")
                .phone(c != null ? c.getPhone() : "")
                .address(c != null ? c.getAddress() : "")
                .enabled(u.getEnabled())
                .createdAt(u.getCreatedAt())
                .lastLogin(u.getLastLogin())
                .build();
    }
}
