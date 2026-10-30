# BankEase – NextGen Net Banking: Architectural Documentation

## 1. System Overview
**BankEase** is an enterprise-grade digital banking web solution engineered for academic excellence and commercial-grade architecture demonstrations. It adopts a modern decoupled architecture where an Angular single-page application (SPA) communicates via secure RESTful APIs with a robust Spring Boot 3.3 micro-monolith, supported by a MySQL 8.x relational persistence store.

---

## 2. High-Level Architecture Diagram

```
+------------------------------------------------------------------------+
|                            USER BROWSER                                |
|  [Desktop / Tablet / Mobile] - Responsive Angular SPA                  |
+------------------------------------------------------------------------+
                                   |
                         HTTPS / JSON / JWT
                                   |
                                   v
+------------------------------------------------------------------------+
|                          SPRING BOOT BACKEND                           |
|                                                                        |
|  +------------------------------------------------------------------+  |
|  |                     SECURITY FILTER CHAIN                        |  |
|  |  - CorsFilter & CsrfFilter (Stateless API Protection)            |  |
|  |  - JwtAuthenticationFilter (Token Parsing & Validation)          |  |
|  |  - Role-Based Access Control (CUSTOMER / BANK_STAFF / ADMIN)     |  |
|  +------------------------------------------------------------------+  |
|                                  |                                     |
|  +------------------------------------------------------------------+  |
|  |                       REST CONTROLLER LAYER                      |  |
|  |  AuthController, AccountController, TransferController,         |  |
|  |  BillPaymentController, LoanController, InvestmentController,    |  |
|  |  NotificationController, AdminController, StaffController        |  |
|  +------------------------------------------------------------------+  |
|                                  |                                     |
|  +------------------------------------------------------------------+  |
|  |                     SERVICE & BUSINESS LOGIC                     |  |
|  |  - Atomic Transactions (@Transactional, READ_COMMITTED)          |  |
|  |  - Pessimistic Row Locking (PESSIMISTIC_WRITE on Accounts)       |  |
|  |  - Idempotency Guarantee Engine                                  |  |
|  |  - Automated EMI & Compounded Returns Computation               |  |
|  |  - Multi-Factor Simulated OTP Generator                          |  |
|  |  - Centralized Audit Logging System                              |  |
|  +------------------------------------------------------------------+  |
|                                  |                                     |
|  +------------------------------------------------------------------+  |
|  |                       PERSISTENCE LAYER                          |  |
|  |  Spring Data JPA, Hibernate ORM, Flyway Migrations              |  |
|  +------------------------------------------------------------------+  |
+------------------------------------------------------------------------+
                                   |
                          JDBC Connection Pool
                               (HikariCP)
                                   |
                                   v
+------------------------------------------------------------------------+
|                            MYSQL 8.0                                   |
|  Database: bankease_db                                                 |
|  - Users, Customers, Accounts, Transactions, Transfers, Bills,       |
|    Loans, Investments, Notifications, Audit Logs, Idempotency Records  |
+------------------------------------------------------------------------+
```

---

## 3. Core Architectural Modules

### 3.1 Authentication & Authorization
- **Stateless Tokens**: Uses JSON Web Tokens (JJWT 0.12.x) with HMAC-SHA256 signature verification.
- **Credential Storage**: Passwords are never stored in plain text; salted hashing is performed using `BCryptPasswordEncoder` (strength 10).
- **Role Hierarchy**:
  - `CUSTOMER`: Limited to owned accounts, transfers, bills, loan applications, investments.
  - `BANK_STAFF`: Can review and approve/reject loan applications, verify investments, search customer details.
  - `ADMIN`: Comprehensive administrative visibility including user activation/deactivation, account freezing, system metric monitoring, and audit log inspection.

### 3.2 Concurrency & Transactional Integrity
- **ACID Guarantees**: All financial transfers use Spring's `@Transactional` annotation.
- **Pessimistic Locking**: `AccountRepository.findByAccountIdWithLock()` uses `LockModeType.PESSIMISTIC_WRITE` (generates `SELECT ... FOR UPDATE`), preventing concurrent race conditions and double-spending.
- **Precision Monetary Math**: Every financial computation utilizes `BigDecimal` to prevent floating-point rounding inaccuracies.
- **Idempotency Protection**: Every transfer can carry an `idempotencyKey`. Duplicate requests with identical keys are resolved from the `idempotency_records` table without double debits.

---

## 4. Frontend Component Architecture
The Angular application uses standalone components and services:
- **`AuthGuard` & `RoleGuard`**: Protects routes on the client side according to user roles.
- **`JwtInterceptor`**: Injects `Authorization: Bearer <token>` to all HTTP requests and handles HTTP 401/403 redirects.
- **State Management**: Reactive signals and RxJS BehaviorSubjects retain active user session, notifications unread counts, and active account selection.

