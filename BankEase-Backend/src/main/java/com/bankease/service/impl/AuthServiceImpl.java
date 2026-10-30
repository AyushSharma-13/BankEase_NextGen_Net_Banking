package com.bankease.service.impl;

import com.bankease.dto.request.ChangePasswordRequest;
import com.bankease.dto.request.LoginRequest;
import com.bankease.dto.request.RegisterRequest;
import com.bankease.dto.response.AuthResponse;
import com.bankease.dto.response.UserDto;
import com.bankease.entity.*;
import com.bankease.exception.BadRequestException;
import com.bankease.exception.DuplicateResourceException;
import com.bankease.exception.ResourceNotFoundException;
import com.bankease.repository.AccountRepository;
import com.bankease.repository.CustomerRepository;
import com.bankease.repository.TransactionRepository;
import com.bankease.repository.UserRepository;
import com.bankease.security.JwtTokenProvider;
import com.bankease.security.SecurityUtils;
import com.bankease.security.UserPrincipal;
import com.bankease.service.AuditService;
import com.bankease.service.AuthService;
import com.bankease.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String customerName = user.getCustomer() != null ? user.getCustomer().getName() : user.getUsername();
        String email = user.getCustomer() != null ? user.getCustomer().getEmail() : "";

        auditService.log(user, "USER_LOGIN", "/api/auth/login", "127.0.0.1", "Successful login for " + user.getUsername());

        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .customerId(user.getCustomer() != null ? user.getCustomer().getCustomerId() : null)
                .customerName(customerName)
                .email(email)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already registered");
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered");
        }

        // 1. Create Customer
        Customer customer = Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();
        Customer savedCustomer = customerRepository.saveAndFlush(customer);

        // 2. Create User
        User user = User.builder()
                .customer(savedCustomer)
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .enabled(true)
                .lastLogin(LocalDateTime.now())
                .build();
        User savedUser = userRepository.saveAndFlush(user);

        // 3. Create Default Primary Account
        String accountNo = generateUniqueAccountNumber();
        BigDecimal initialDeposit = request.getInitialDeposit() != null && request.getInitialDeposit().compareTo(BigDecimal.ZERO) >= 0
                ? request.getInitialDeposit() : BigDecimal.valueOf(1000.00);

        Account account = Account.builder()
                .customer(savedCustomer)
                .accountNo(accountNo)
                .accountType(request.getAccountType() != null ? request.getAccountType().toUpperCase() : "SAVINGS")
                .balance(initialDeposit)
                .status("ACTIVE")
                .build();
        Account savedAccount = accountRepository.save(account);

        // 4. Initial Transaction Record
        if (initialDeposit.compareTo(BigDecimal.ZERO) > 0) {
            Transaction initialTxn = Transaction.builder()
                    .account(savedAccount)
                    .type("CREDIT")
                    .amount(initialDeposit)
                    .balanceAfter(initialDeposit)
                    .status("COMPLETED")
                    .description("Initial Opening Deposit")
                    .referenceNo("TXN-INIT-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                    .build();
            transactionRepository.save(initialTxn);
        }

        // 5. Welcome Notification
        notificationService.createNotification(
                savedCustomer.getCustomerId(),
                "Welcome to BankEase Net Banking!",
                "Your " + savedAccount.getAccountType() + " account " + savedAccount.getAccountNo() + " has been successfully created with an opening balance of Rs. " + initialDeposit + ".",
                "SYSTEM"
        );

        auditService.log(savedUser, "CUSTOMER_REGISTRATION", "/api/auth/register", "127.0.0.1", "Registered new customer: " + savedUser.getUsername());

        // Authenticate new user and return JWT
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String jwt = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .userId(savedUser.getUserId())
                .username(savedUser.getUsername())
                .role(savedUser.getRole().name())
                .customerId(savedCustomer.getCustomerId())
                .customerName(savedCustomer.getName())
                .email(savedCustomer.getEmail())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getCurrentUserProfile() {
        UserPrincipal principal = SecurityUtils.getCurrentUser();
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Customer customer = user.getCustomer();

        return UserDto.builder()
                .userId(user.getUserId())
                .customerId(customer != null ? customer.getCustomerId() : null)
                .username(user.getUsername())
                .role(user.getRole().name())
                .name(customer != null ? customer.getName() : user.getUsername())
                .email(customer != null ? customer.getEmail() : "")
                .phone(customer != null ? customer.getPhone() : "")
                .address(customer != null ? customer.getAddress() : "")
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        UserPrincipal principal = SecurityUtils.getCurrentUser();
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        if (user.getCustomer() != null) {
            notificationService.createNotification(
                    user.getCustomer().getCustomerId(),
                    "Security Alert: Password Changed",
                    "Your BankEase login password was updated recently. If this wasn't you, please contact support immediately.",
                    "SECURITY"
            );
        }

        auditService.log(user, "CHANGE_PASSWORD", "/api/auth/change-password", "127.0.0.1", "Password updated for " + user.getUsername());
    }

    private String generateUniqueAccountNumber() {
        String accountNo;
        do {
            long randomDigits = ThreadLocalRandom.current().nextLong(10000000L, 99999999L);
            accountNo = "1000" + randomDigits;
        } while (accountRepository.findByAccountNo(accountNo).isPresent());
        return accountNo;
    }
}
