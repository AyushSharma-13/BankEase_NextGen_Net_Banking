# BankEase – NextGen Net Banking: Database Design Documentation

## 1. Database Model & Normalization
The BankEase database (`bankease_db`) is normalized to 3rd Normal Form (3NF) to eliminate data redundancy while maintaining referential integrity.

### Entity Relationship Model

```
+--------------------+           +--------------------+
|     CUSTOMERS      | 1 ----- 1 |       USERS        |
+--------------------+           +--------------------+
| customer_id (PK)   |           | user_id (PK)       |
| name               |           | customer_id (FK)   |
| email (UNIQUE)     |           | username (UNIQUE)  |
| phone              |           | password_hash      |
| address            |           | role               |
| created_at         |           | enabled            |
+--------------------+           +--------------------+
        | 1                                 |
        |                                   | 1
        | 1..*                              |
+--------------------+                      | 1..*
|      ACCOUNTS      |             +--------------------+
+--------------------+             |     AUDIT_LOGS     |
| account_id (PK)    |             +--------------------+
| customer_id (FK)   |             | audit_id (PK)      |
| account_no (UNIQUE)|             | user_id (FK)       |
| account_type       |             | action             |
| balance            |             | endpoint           |
| status             |             | ip_address         |
| created_at         |             | details            |
+--------------------+             | timestamp          |
   | 1            | 1              +--------------------+
   |              |
   | 1..*         | 1..*
+-------------+  +-------------+   +--------------------+
|TRANSACTIONS |  |  TRANSFERS  |   |   BILL_PAYMENTS    |
+-------------+  +-------------+   +--------------------+
|txn_id (PK)  |  |transfer_id  |   | bill_id (PK)       |
|account_id FK|  |source_acc FK|   | account_id (FK)    |
|type         |  |dest_acc_no  |   | customer_id (FK)   |
|amount       |  |beneficiaryFK|   | category           |
|balance_after|  |amount       |   | biller_name        |
|status       |  |status       |   | consumer_number    |
|reference_no |  |reference_no |   | amount             |
|created_at   |  |created_at   |   | status             |
+-------------+  +-------------+   | reference_no       |
                                   | created_at         |
                                   +--------------------+
```

---

## 2. Table Catalog

| Table Name | Primary Key | Foreign Keys | Key Purpose |
|---|---|---|---|
| `customers` | `customer_id` | - | Customer identity and demographics |
| `users` | `user_id` | `customer_id` | Credentials, JWT auth, system role |
| `accounts` | `account_id` | `customer_id` | Deposit ledger, account number, balance |
| `beneficiaries` | `beneficiary_id` | `customer_id` | Pre-approved money transfer contacts |
| `transactions` | `transaction_id` | `account_id` | Immutable double-entry transaction record |
| `transfers` | `transfer_id` | `source_account_id`, `beneficiary_id` | Internal/External transfer history |
| `bill_payments` | `bill_id` | `account_id`, `customer_id` | Utility payments tracking |
| `loans` | `loan_id` | `customer_id` | Loan application, EMI, approval status |
| `investments` | `investment_id` | `customer_id` | FDs, RDs, Mutual Funds, NPS records |
| `notifications` | `notification_id` | `customer_id` | Transaction alerts, security warnings |
| `otp_verifications` | `otp_id` | `user_id` | Time-bounded simulated OTP verification |
| `audit_logs` | `audit_id` | `user_id` | Operational audit trail |
| `idempotency_records`| `idempotency_key` | - | Protection against duplicate transaction submission |

