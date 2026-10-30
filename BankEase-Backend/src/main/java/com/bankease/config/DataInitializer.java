package com.bankease.config;

import com.bankease.entity.*;
import com.bankease.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;
    private final BillPaymentRepository billPaymentRepository;
    private final LoanRepository loanRepository;
    private final InvestmentRepository investmentRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded with {} users. Ensuring passwords are correctly encoded.", userRepository.count());
            ensureUserPasswords();
            return;
        }

        log.info("Initializing BankEase database with demonstration data...");

        // 1. Create Customers
        Customer cRajesh = customerRepository.save(Customer.builder()
                .name("Rajesh Sharma")
                .email("rajesh.sharma@example.com")
                .phone("+91 98765 43210")
                .address("42 MG Road, Bangalore, Karnataka - 560001")
                .build());

        Customer cPriya = customerRepository.save(Customer.builder()
                .name("Priya Patel")
                .email("priya.patel@example.com")
                .phone("+91 98234 56789")
                .address("15 Nehru Place, New Delhi - 110019")
                .build());

        Customer cArun = customerRepository.save(Customer.builder()
                .name("Arun Kumar")
                .email("arun.kumar@example.com")
                .phone("+91 97123 45678")
                .address("88 Anna Salai, Chennai, Tamil Nadu - 600002")
                .build());

        Customer cStaff = customerRepository.save(Customer.builder()
                .name("Bank Staff Amit")
                .email("amit.verma@bankease.internal")
                .phone("+91 99999 11111")
                .address("BankEase HQ Operations, Mumbai")
                .build());

        Customer cAdmin = customerRepository.save(Customer.builder()
                .name("System Administrator")
                .email("admin@bankease.internal")
                .phone("+91 99999 00000")
                .address("BankEase Corporate Office, Mumbai")
                .build());

        // 2. Create Users
        String encodedCustomerPass = passwordEncoder.encode("Password@123");
        String encodedAdminPass = passwordEncoder.encode("Admin@123");

        userRepository.save(User.builder()
                .customer(cRajesh)
                .username("rajesh")
                .passwordHash(encodedCustomerPass)
                .role(Role.CUSTOMER)
                .enabled(true)
                .build());

        userRepository.save(User.builder()
                .customer(cPriya)
                .username("priya")
                .passwordHash(encodedCustomerPass)
                .role(Role.CUSTOMER)
                .enabled(true)
                .build());

        userRepository.save(User.builder()
                .customer(cArun)
                .username("arun")
                .passwordHash(encodedCustomerPass)
                .role(Role.CUSTOMER)
                .enabled(true)
                .build());

        userRepository.save(User.builder()
                .customer(cStaff)
                .username("staff")
                .passwordHash(encodedCustomerPass)
                .role(Role.BANK_STAFF)
                .enabled(true)
                .build());

        userRepository.save(User.builder()
                .customer(cAdmin)
                .username("admin")
                .passwordHash(encodedAdminPass)
                .role(Role.ADMIN)
                .enabled(true)
                .build());

        // 3. Create Accounts
        Account a1 = accountRepository.save(Account.builder()
                .customer(cRajesh)
                .accountNo("100023456781")
                .accountType("SAVINGS")
                .balance(BigDecimal.valueOf(154250.00))
                .status("ACTIVE")
                .build());

        Account a2 = accountRepository.save(Account.builder()
                .customer(cRajesh)
                .accountNo("100023456782")
                .accountType("CURRENT")
                .balance(BigDecimal.valueOf(48500.00))
                .status("ACTIVE")
                .build());

        Account a3 = accountRepository.save(Account.builder()
                .customer(cPriya)
                .accountNo("100098765431")
                .accountType("SAVINGS")
                .balance(BigDecimal.valueOf(89200.00))
                .status("ACTIVE")
                .build());

        Account a4 = accountRepository.save(Account.builder()
                .customer(cArun)
                .accountNo("100055443321")
                .accountType("SAVINGS")
                .balance(BigDecimal.valueOf(215000.00))
                .status("ACTIVE")
                .build());

        // 4. Beneficiaries
        Beneficiary b1 = beneficiaryRepository.save(Beneficiary.builder()
                .customer(cRajesh)
                .name("Priya Patel")
                .accountNo("100098765431")
                .bankName("BankEase Net Bank")
                .ifscCode("BEAS0001001")
                .status("ACTIVE")
                .build());

        Beneficiary b2 = beneficiaryRepository.save(Beneficiary.builder()
                .customer(cRajesh)
                .name("Arun Kumar")
                .accountNo("100055443321")
                .bankName("BankEase Net Bank")
                .ifscCode("BEAS0001001")
                .status("ACTIVE")
                .build());

        beneficiaryRepository.save(Beneficiary.builder()
                .customer(cPriya)
                .name("Rajesh Sharma")
                .accountNo("100023456781")
                .bankName("BankEase Net Bank")
                .ifscCode("BEAS0001001")
                .status("ACTIVE")
                .build());

        // 5. Initial Transactions
        transactionRepository.save(Transaction.builder()
                .account(a1)
                .type("CREDIT")
                .amount(BigDecimal.valueOf(100000.00))
                .balanceAfter(BigDecimal.valueOf(100000.00))
                .status("COMPLETED")
                .description("Initial Opening Deposit")
                .referenceNo("TXN-BEAS-SEED-001")
                .build());

        transactionRepository.save(Transaction.builder()
                .account(a1)
                .type("CREDIT")
                .amount(BigDecimal.valueOf(75000.00))
                .balanceAfter(BigDecimal.valueOf(175000.00))
                .status("COMPLETED")
                .description("Salary Credit - TechCorp Global")
                .referenceNo("TXN-BEAS-SEED-002")
                .build());

        transactionRepository.save(Transaction.builder()
                .account(a1)
                .type("DEBIT")
                .amount(BigDecimal.valueOf(15000.00))
                .balanceAfter(BigDecimal.valueOf(160000.00))
                .status("COMPLETED")
                .description("Transfer to Priya Patel")
                .referenceNo("TXN-BEAS-SEED-003")
                .build());

        transactionRepository.save(Transaction.builder()
                .account(a1)
                .type("DEBIT")
                .amount(BigDecimal.valueOf(3250.00))
                .balanceAfter(BigDecimal.valueOf(156750.00))
                .status("COMPLETED")
                .description("Electricity Bill - BESCOM Bangalore")
                .referenceNo("TXN-BEAS-SEED-004")
                .build());

        transactionRepository.save(Transaction.builder()
                .account(a1)
                .type("DEBIT")
                .amount(BigDecimal.valueOf(2500.00))
                .balanceAfter(BigDecimal.valueOf(154250.00))
                .status("COMPLETED")
                .description("Internet Fiber Bill - Airtel Broadband")
                .referenceNo("TXN-BEAS-SEED-005")
                .build());

        // 6. Transfer
        transferRepository.save(Transfer.builder()
                .sourceAccount(a1)
                .destinationAccountNo("100098765431")
                .beneficiary(b1)
                .amount(BigDecimal.valueOf(15000.00))
                .status("COMPLETED")
                .referenceNo("TRF-BEAS-SEED-001")
                .remarks("Monthly Maintenance")
                .build());

        // 7. Bill Payments
        billPaymentRepository.save(BillPayment.builder()
                .account(a1)
                .customer(cRajesh)
                .category("ELECTRICITY")
                .billerName("BESCOM Bangalore")
                .consumerNumber("BES-99887766")
                .amount(BigDecimal.valueOf(3250.00))
                .status("PAID")
                .referenceNo("BILL-BEAS-SEED-001")
                .build());

        billPaymentRepository.save(BillPayment.builder()
                .account(a1)
                .customer(cRajesh)
                .category("INTERNET")
                .billerName("Airtel Broadband Fiber")
                .consumerNumber("AIR-080-456789")
                .amount(BigDecimal.valueOf(2500.00))
                .status("PAID")
                .referenceNo("BILL-BEAS-SEED-002")
                .build());

        // 8. Loans
        loanRepository.save(Loan.builder()
                .customer(cRajesh)
                .loanType("HOME")
                .amount(BigDecimal.valueOf(3500000.00))
                .interestRate(BigDecimal.valueOf(8.50))
                .tenureMonths(180)
                .monthlyEmi(BigDecimal.valueOf(34487.50))
                .status("UNDER_REVIEW")
                .remarks("Property in Bangalore East. KYC documents verified.")
                .build());

        loanRepository.save(Loan.builder()
                .customer(cPriya)
                .loanType("PERSONAL")
                .amount(BigDecimal.valueOf(200000.00))
                .interestRate(BigDecimal.valueOf(11.25))
                .tenureMonths(24)
                .monthlyEmi(BigDecimal.valueOf(9345.00))
                .status("APPROVED")
                .remarks("Instant approval based on credit rating.")
                .build());

        // 9. Investments
        investmentRepository.save(Investment.builder()
                .customer(cRajesh)
                .investmentType("FIXED_DEPOSIT")
                .amount(BigDecimal.valueOf(100000.00))
                .tenureMonths(12)
                .expectedReturnRate(BigDecimal.valueOf(7.10))
                .status("ACTIVE")
                .build());

        investmentRepository.save(Investment.builder()
                .customer(cRajesh)
                .investmentType("MUTUAL_FUNDS")
                .amount(BigDecimal.valueOf(25000.00))
                .tenureMonths(36)
                .expectedReturnRate(BigDecimal.valueOf(12.50))
                .status("ACTIVE")
                .build());

        // 10. Notifications
        notificationRepository.save(Notification.builder()
                .customer(cRajesh)
                .title("Welcome to BankEase")
                .message("Welcome to BankEase NextGen Net Banking! Your accounts are active and ready for online services.")
                .type("SYSTEM")
                .isRead(true)
                .build());

        notificationRepository.save(Notification.builder()
                .customer(cRajesh)
                .title("Transfer Completed")
                .message("Rs. 15,000.00 successfully transferred to Priya Patel. Ref: TRF-BEAS-SEED-001")
                .type("TRANSACTION")
                .isRead(true)
                .build());

        notificationRepository.save(Notification.builder()
                .customer(cRajesh)
                .title("Home Loan Application Under Review")
                .message("Your application for a Home Loan of Rs. 35,00,000.00 is currently under review by our credit verification team.")
                .type("LOAN")
                .isRead(false)
                .build());

        notificationRepository.save(Notification.builder()
                .customer(cRajesh)
                .title("Bill Payment Successful")
                .message("Electricity bill of Rs. 3,250.00 paid to BESCOM Bangalore. Ref: BILL-BEAS-SEED-001")
                .type("TRANSACTION")
                .isRead(false)
                .build());

        log.info("BankEase database initialization completed successfully.");
    }

    private void ensureUserPasswords() {
        // Ensure standard users can log in even if seeded via SQL with a different salt
        userRepository.findByUsername("rajesh").ifPresent(u -> {
            u.setPasswordHash(passwordEncoder.encode("Password@123"));
            userRepository.save(u);
        });
        userRepository.findByUsername("priya").ifPresent(u -> {
            u.setPasswordHash(passwordEncoder.encode("Password@123"));
            userRepository.save(u);
        });
        userRepository.findByUsername("arun").ifPresent(u -> {
            u.setPasswordHash(passwordEncoder.encode("Password@123"));
            userRepository.save(u);
        });
        userRepository.findByUsername("staff").ifPresent(u -> {
            u.setPasswordHash(passwordEncoder.encode("Password@123"));
            userRepository.save(u);
        });
        userRepository.findByUsername("admin").ifPresent(u -> {
            u.setPasswordHash(passwordEncoder.encode("Admin@123"));
            userRepository.save(u);
        });
    }
}
