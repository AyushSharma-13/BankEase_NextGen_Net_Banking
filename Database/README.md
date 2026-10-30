# BankEase – NextGen Net Banking: Database Documentation

## Overview
The `bankease_db` database is built on MySQL 8.x and strictly enforces ACID transaction guarantees, referential integrity with foreign key constraints, proper decimal precision for monetary operations (`DECIMAL(15,2)`), and indexing on critical search fields.

## Schema Details
- **`customers`**: Holds core demographic and contact details.
- **`users`**: Manages credentials, roles (`CUSTOMER`, `BANK_STAFF`, `ADMIN`), and authentication status.
- **`accounts`**: Stores account balances, account numbers, and statuses (`ACTIVE`, `FROZEN`, `CLOSED`).
- **`beneficiaries`**: Pre-registered recipients for domestic fund transfers.
- **`transactions`**: Double-entry ledger recording all debits and credits with balance tracking.
- **`transfers`**: Records inter-account and external beneficiary fund transfers.
- **`bill_payments`**: Captures utility bill payments (Electricity, Water, Mobile, Internet, Gas).
- **`loans`**: Handles loan applications (Home, Personal, Education, Vehicle) and review status.
- **`investments`**: Tracks investments (Fixed Deposits, Recurring Deposits, Mutual Funds, NPS).
- **`notifications`**: User alerts and transactional notifications.
- **`otp_verifications`**: Time-limited simulated OTP verification tokens.
- **`audit_logs`**: System audit trail capturing sensitive administrative and financial actions.
- **`idempotency_records`**: Guarantees transfer idempotency to prevent duplicate charges.

## Setup Instructions

### 1. Manual Setup using MySQL Workbench / CLI
```bash
mysql -u root -p < schema.sql
mysql -u root -p < sample-data.sql
```

### 2. Automatic Setup with Spring Boot
The Spring Boot backend utilizes Flyway migrations (`V1__initial_schema.sql` and `V2__seed_data.sql`) along with a startup `DataInitializer` to automatically create tables, insert seeds, and securely hash passwords upon startup.
