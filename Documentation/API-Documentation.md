# BankEase – NextGen Net Banking: REST API Specification

All API endpoints return a standardized envelope structure:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2026-10-30T22:30:00"
}
```

---

## 1. Authentication APIs (`/api/auth`)

### `POST /api/auth/login`
- **Access**: Public
- **Request Body**:
  ```json
  {
    "username": "rajesh",
    "password": "Password@123"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "message": "Authentication successful",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsIn...",
      "tokenType": "Bearer",
      "userId": 1,
      "username": "rajesh",
      "role": "CUSTOMER",
      "customerId": 1,
      "customerName": "Rajesh Sharma",
      "email": "rajesh.sharma@example.com"
    }
  }
  ```

### `POST /api/auth/register`
- **Access**: Public
- **Request Body**:
  ```json
  {
    "name": "Karan Malhotra",
    "email": "karan.malhotra@example.com",
    "phone": "+91 98111 22233",
    "address": "55 Linking Road, Bandra, Mumbai",
    "username": "karanm",
    "password": "Password@123",
    "accountType": "SAVINGS",
    "initialDeposit": 5000.00
  }
  ```

---

## 2. Account APIs (`/api/accounts`)

### `GET /api/accounts`
- **Headers**: `Authorization: Bearer <token>`
- **Response**: List of accounts owned by the authenticated customer.

### `GET /api/accounts/dashboard`
- **Headers**: `Authorization: Bearer <token>`
- **Response**: Aggregated balances, account list, top 10 recent transactions, unread notification count, active loans and investments.

### `GET /api/accounts/{id}/statement`
- **Headers**: `Authorization: Bearer <token>`
- **Response**: Plain text downloadable account statement.

---

## 3. Beneficiary APIs (`/api/beneficiaries`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/beneficiaries` | List approved beneficiaries |
| `POST` | `/api/beneficiaries` | Add new beneficiary |
| `PUT` | `/api/beneficiaries/{id}` | Update existing beneficiary details |
| `DELETE`| `/api/beneficiaries/{id}` | Remove beneficiary |
| `GET` | `/api/beneficiaries/validate/{accountNo}` | Validate beneficiary account format |

---

## 4. Fund Transfer APIs (`/api/transfers`)

### `POST /api/transfers/otp`
- **Headers**: `Authorization: Bearer <token>`
- **Description**: Generates a 6-digit simulated OTP for 2-factor authentication. Default test OTP is `123456`.

### `POST /api/transfers`
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
  ```json
  {
    "sourceAccountId": 1,
    "destinationAccountNo": "100098765431",
    "beneficiaryId": 1,
    "amount": 2500.00,
    "remarks": "Project freelance payment",
    "otpCode": "123456",
    "idempotencyKey": "req-uuid-12345"
  }
  ```

---

## 5. Bill Payment APIs (`/api/bills`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/bills/categories` | Returns ELECTRICITY, WATER, MOBILE_RECHARGE, INTERNET, GAS |
| `POST` | `/api/bills/pay` | Pay utility bill and deduct balance |
| `GET` | `/api/bills/history` | Paginated bill payment history |

---

## 6. Loan Management APIs (`/api/loans`)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| `GET` | `/api/loans/types` | Any | Available loan products and interest rates |
| `POST` | `/api/loans/apply` | Customer | Submit loan application |
| `GET` | `/api/loans` | Customer | List applied loans |
| `GET` | `/api/staff/loans` | Staff/Admin | List all submitted loans across the bank |
| `PATCH`| `/api/staff/loans/{id}/status`| Staff/Admin | Update loan status (`APPROVED`/`REJECTED`) |

---

## 7. Investment Management APIs (`/api/investments`)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| `GET` | `/api/investments/plans` | Any | Available investment schemes (FD, RD, MF, NPS) |
| `POST` | `/api/investments` | Customer | Start new investment portfolio |
| `GET` | `/api/investments` | Customer | List user's investments |
| `GET` | `/api/staff/investments` | Staff/Admin | List all customer investments |

---

## 8. Admin APIs (`/api/admin`)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| `GET` | `/api/admin/metrics` | Admin | Total accounts, deposits, users, transactions |
| `GET` | `/api/admin/users` | Admin | Manage bank users |
| `PATCH`| `/api/admin/users/{id}/toggle`| Admin | Enable or disable user access |
| `GET` | `/api/admin/accounts` | Admin | Review all bank accounts |
| `PATCH`| `/api/admin/accounts/{id}/status`| Admin | Set account status (`ACTIVE`, `FROZEN`, `CLOSED`) |
| `GET` | `/api/admin/audit-logs` | Admin | Inspect full system audit log |
| `POST` | `/api/admin/staff` | Admin | Create bank employee account |
