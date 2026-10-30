package com.bankease;

import com.bankease.dto.request.LoginRequest;
import com.bankease.dto.request.RegisterRequest;
import com.bankease.dto.response.AuthResponse;
import com.bankease.repository.AccountRepository;
import com.bankease.repository.CustomerRepository;
import com.bankease.repository.UserRepository;
import com.bankease.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Should successfully register a new customer and auto-provision bank account")
    void testRegisterNewCustomer() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Rohan Mehta")
                .email("rohan.mehta@example.com")
                .phone("+91 91234 56780")
                .address("77 Park Street, Kolkata")
                .username("rohanmehta")
                .password("Password@123")
                .accountType("SAVINGS")
                .initialDeposit(BigDecimal.valueOf(5000.00))
                .build();

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("rohanmehta", response.getUsername());
        assertEquals("CUSTOMER", response.getRole());

        // Verify entities in database
        assertTrue(userRepository.existsByUsername("rohanmehta"));
        assertTrue(customerRepository.existsByEmail("rohan.mehta@example.com"));

        var accounts = accountRepository.findByCustomerCustomerId(response.getCustomerId());
        assertEquals(1, accounts.size());
        assertEquals(0, BigDecimal.valueOf(5000.00).compareTo(accounts.get(0).getBalance()));
    }

    @Test
    @DisplayName("Should successfully login existing user and return valid JWT")
    void testLoginExistingUser() {
        // Register user first
        RegisterRequest registerReq = RegisterRequest.builder()
                .name("Kavita Joshi")
                .email("kavita.joshi@example.com")
                .phone("+91 99887 66554")
                .address("90 Ring Road, Pune")
                .username("kavitajoshi")
                .password("Password@123")
                .initialDeposit(BigDecimal.valueOf(2000.00))
                .build();
        authService.register(registerReq);

        // Login
        LoginRequest loginReq = LoginRequest.builder()
                .username("kavitajoshi")
                .password("Password@123")
                .build();

        AuthResponse loginRes = authService.login(loginReq);
        assertNotNull(loginRes);
        assertNotNull(loginRes.getToken());
        assertEquals("kavitajoshi", loginRes.getUsername());
    }
}
