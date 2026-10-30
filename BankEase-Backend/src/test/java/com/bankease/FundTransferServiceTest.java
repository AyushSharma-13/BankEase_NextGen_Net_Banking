package com.bankease;

import com.bankease.dto.request.TransferRequest;
import com.bankease.dto.response.TransferResponse;
import com.bankease.entity.Account;
import com.bankease.entity.Customer;
import com.bankease.entity.User;
import com.bankease.exception.InsufficientBalanceException;
import com.bankease.repository.AccountRepository;
import com.bankease.repository.CustomerRepository;
import com.bankease.repository.UserRepository;
import com.bankease.service.FundTransferService;
import org.junit.jupiter.api.BeforeEach;
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
class FundTransferServiceTest {

    @Autowired
    private FundTransferService fundTransferService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Customer customerA;
    private Customer customerB;
    private User userA;
    private Account accountA;
    private Account accountB;

    @BeforeEach
    void setUp() {
        customerA = customerRepository.save(Customer.builder()
                .name("Alice Test")
                .email("alice.test@example.com")
                .phone("+91 99999 12345")
                .address("123 Test Street")
                .build());

        customerB = customerRepository.save(Customer.builder()
                .name("Bob Test")
                .email("bob.test@example.com")
                .phone("+91 99999 67890")
                .address("456 Sample Ave")
                .build());

        userA = userRepository.save(User.builder()
                .customer(customerA)
                .username("alicetest")
                .passwordHash("hashedPass")
                .build());

        accountA = accountRepository.save(Account.builder()
                .customer(customerA)
                .accountNo("100099990001")
                .accountType("SAVINGS")
                .balance(BigDecimal.valueOf(10000.00))
                .status("ACTIVE")
                .build());

        accountB = accountRepository.save(Account.builder()
                .customer(customerB)
                .accountNo("100099990002")
                .accountType("SAVINGS")
                .balance(BigDecimal.valueOf(5000.00))
                .status("ACTIVE")
                .build());
    }

    @Test
    @DisplayName("Should process fund transfer successfully with atomic debit and credit")
    void testProcessTransferSuccess() {
        TransferRequest request = TransferRequest.builder()
                .sourceAccountId(accountA.getAccountId())
                .destinationAccountNo(accountB.getAccountNo())
                .amount(BigDecimal.valueOf(2500.00))
                .otpCode("123456")
                .remarks("Test Rent Transfer")
                .build();

        TransferResponse response = fundTransferService.processTransfer(
                customerA.getCustomerId(),
                userA.getUserId(),
                request
        );

        assertNotNull(response);
        assertEquals("COMPLETED", response.getStatus());
        assertEquals(BigDecimal.valueOf(2500.00), response.getAmount());
        assertNotNull(response.getReferenceNo());

        // Verify balances after atomic transfer
        Account updatedA = accountRepository.findById(accountA.getAccountId()).orElseThrow();
        Account updatedB = accountRepository.findById(accountB.getAccountId()).orElseThrow();

        assertEquals(0, BigDecimal.valueOf(7500.00).compareTo(updatedA.getBalance()));
        assertEquals(0, BigDecimal.valueOf(7500.00).compareTo(updatedB.getBalance()));
    }

    @Test
    @DisplayName("Should reject transfer when balance is insufficient")
    void testProcessTransferInsufficientBalance() {
        TransferRequest request = TransferRequest.builder()
                .sourceAccountId(accountA.getAccountId())
                .destinationAccountNo(accountB.getAccountNo())
                .amount(BigDecimal.valueOf(25000.00)) // exceeds 10,000 balance
                .otpCode("123456")
                .remarks("Overdraft Attempt")
                .build();

        assertThrows(InsufficientBalanceException.class, () ->
                fundTransferService.processTransfer(
                        customerA.getCustomerId(),
                        userA.getUserId(),
                        request
                )
        );

        // Verify source balance remains unchanged
        Account unchangedA = accountRepository.findById(accountA.getAccountId()).orElseThrow();
        assertEquals(0, BigDecimal.valueOf(10000.00).compareTo(unchangedA.getBalance()));
    }
}

