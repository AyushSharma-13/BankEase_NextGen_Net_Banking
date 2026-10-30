# BankEase – NextGen Net Banking: Backend API Service

## Overview
The **BankEase Backend** is an enterprise-grade RESTful web service built with Java 21 LTS, Spring Boot 3.3, Spring Security 6, Spring Data JPA, and MySQL. It powers all core banking transactions, multi-role security, account operations, fund transfers, bill payments, loan approvals, and investment tracking.

---

## Key Capabilities

1. **Authentication & Authorization**:
   - Stateless JWT tokens (`io.jsonwebtoken 0.12.6`)
   - BCrypt password hashing
   - Role-Based Access Control (`CUSTOMER`, `BANK_STAFF`, `ADMIN`)
2. **Account Management**:
   - Savings, Current, and Salary accounts
   - Real-time balance inquiry and aggregated dashboard statistics
   - Account status lifecycle (`ACTIVE`, `FROZEN`, `CLOSED`)
3. **Fund Transfers**:
   - Atomic debit and credit operations (`@Transactional`)
   - Database row-level locking (`PESSIMISTIC_WRITE`) preventing race conditions and double-spending
   - Idempotency guarantees via `IdempotencyRecord`
   - Simulated Multi-Factor Authentication (OTP verification)
4. **Utility Bill Payments**:
   - Supported billers: Electricity, Water, Mobile Recharge, Internet Fiber, Gas
   - Transactional statement generation
5. **Loan Management**:
   - Home, Personal, Education, and Vehicle loans
   - Exact mathematical EMI calculator: \( EMI = \frac{P \times r \times (1+r)^n}{(1+r)^n - 1} \)
   - Multi-tier review workflow: `SUBMITTED` -> `UNDER_REVIEW` -> `APPROVED` / `REJECTED`
6. **Investment Services**:
   - Fixed Deposits (FD), Recurring Deposits (RD), Mutual Funds, and National Pension Scheme (NPS)
7. **Audit & Compliance**:
   - Complete audit trail logging IP address, user, action, and endpoint
   - In-app notification center for transactions, loans, and security events

---

## Technology Stack
- **Java**: 21 LTS
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security 6 + JJWT
- **Persistence**: Spring Data JPA / Hibernate 6
- **Database**: MySQL 8.x (`bankease_db`) / H2 for test profile
- **Migrations**: Flyway
- **Build Tool**: Apache Maven

---

## Running Locally

### Prerequisites
- JDK 21 installed (`java -version`)
- MySQL 8.0 running on `localhost:3306` (or use default dev profile which auto-configures H2 in MySQL compatibility mode)

### Configuration
Edit `src/main/resources/application.properties` or provide environment variables:
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/bankease_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=your_password
export JWT_SECRET=YourSuperSecretKeyGoesHere356
```

### Execution
```bash
# Build and run tests
mvn clean test

# Run application
mvn spring-boot:run
```
The server starts at `http://localhost:8080`.

---

## Default Seed Accounts

| Role | Username | Password | Notes |
|---|---|---|---|
| **Customer** | `rajesh` | `Password@123` | Active accounts, loans, investments, beneficiaries |
| **Customer** | `priya` | `Password@123` | Active savings account |
| **Customer** | `arun` | `Password@123` | Active savings account |
| **Bank Staff** | `staff` | `Password@123` | Can review loans and investments |
| **Administrator** | `admin` | `Admin@123` | Full access to users, metrics, accounts, audit logs |

---

## Core API Endpoints

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| `POST` | `/api/auth/login` | Public | Authenticate user & get JWT token |
| `POST` | `/api/auth/register` | Public | Register customer & provision bank account |
| `GET` | `/api/auth/me` | Authenticated | Retrieve authenticated user profile |
| `GET` | `/api/accounts` | Customer | List customer bank accounts |
| `GET` | `/api/accounts/dashboard` | Customer | Dashboard metrics, balances, recent activity |
| `GET` | `/api/accounts/{id}/statement` | Customer | Download formatted account statement |
| `GET` | `/api/beneficiaries` | Customer | List registered transfer beneficiaries |
| `POST` | `/api/beneficiaries` | Customer | Add new beneficiary |
| `POST` | `/api/transfers/otp` | Customer | Request 6-digit transfer OTP |
| `POST` | `/api/transfers` | Customer | Execute atomic fund transfer |
| `GET` | `/api/transactions` | Customer | Search & filter transaction history |
| `POST` | `/api/bills/pay` | Customer | Pay utility bill |
| `POST` | `/api/loans/apply` | Customer | Submit loan application |
| `GET` | `/api/loans/types` | Authenticated | Get loan types & EMI parameters |
| `POST` | `/api/investments` | Customer | Create investment |
| `GET` | `/api/investments/plans` | Authenticated | Get investment plans & interest rates |
| `GET` | `/api/notifications` | Customer | User notification alerts |
| `GET` | `/api/staff/loans` | Staff/Admin | View pending loans for review |
| `PATCH` | `/api/staff/loans/{id}/status` | Staff/Admin | Approve or reject loan |
| `GET` | `/api/admin/metrics` | Admin | System metrics & banking reserves |
| `GET` | `/api/admin/audit-logs` | Admin | Review security audit logs |

