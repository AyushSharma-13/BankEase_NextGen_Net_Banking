# BankEase – NextGen Net Banking (Frontend)

Modern, accessible, responsive digital banking web application built with **Angular 19 / 22** standalone architecture, TypeScript, HTML5, CSS3, and FontAwesome.

---

## 🏛️ Application Architecture

- **Framework:** Angular 19+ (Standalone Components, Signals reactive state, Functional Guards and Interceptors)
- **Styling:** Custom CSS Banking Design System (Design tokens, CSS Variables, Responsive Grids, Flexbox, High-Contrast Typography)
- **Authentication & Security:** JWT Token Bearer authentication stored in `localStorage`, automatic request interception via `JwtInterceptor`, role-based route access controls via `authGuard` and `roleGuard`.
- **API Client:** Angular `HttpClient` interacting with Spring Boot REST endpoints.

---

## 📁 Project Structure

```
BankEase-Frontend/
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── guards/          # auth.guard.ts, role.guard.ts
│   │   │   ├── interceptors/    # jwt.interceptor.ts
│   │   │   ├── models/          # auth, account, transaction, transfer, beneficiary, bill, loan, investment, admin, notification
│   │   │   └── services/        # auth, account, transfer, beneficiary, bill, loan, investment, admin, notification
│   │   ├── layout/              # Responsive Sidebar, Header, Profile Pill, Notifications Badge
│   │   ├── pages/
│   │   │   ├── landing/         # Marketing landing page with hero, features, security trust badges
│   │   │   ├── login/           # Split-screen login with 1-click test credentials
│   │   │   ├── register/        # Online customer onboarding & account creation
│   │   │   ├── dashboard/       # Central command center, quick actions, balances, recent activity
│   │   │   ├── accounts/        # Accounts overview, statement download, detailed transaction list
│   │   │   ├── transfers/       # Transfer funds, beneficiary picker, simulated OTP modal, receipts
│   │   │   ├── beneficiaries/   # Add, manage, and delete approved beneficiaries
│   │   │   ├── transactions/    # Filterable ledger, search by reference, voucher receipt modal
│   │   │   ├── bills/           # Electricity, Water, Mobile, Gas, Internet bill payments
│   │   │   ├── loans/           # EMI Calculator, Loan product explorer, application modal & tracking
│   │   │   ├── investments/     # Wealth management plans (FD, RD, MF, NPS), ROI Calculator
│   │   │   ├── notifications/   # Real-time alert feed with read/unread tracking
│   │   │   ├── admin/           # System metrics, user enable/disable, account freeze, audit trail
│   │   │   ├── staff/           # Loan underwriter queue, investment approval portal
│   │   │   ├── profile/         # Customer profile viewer & secure password changer
│   │   │   └── not-found/       # 404 Error page
│   │   ├── app.config.ts        # App configuration & HTTP interceptors
│   │   ├── app.routes.ts        # Route declarations & role guards
│   │   └── app.ts               # Root application shell
│   ├── environments/            # Dev and Prod API URL targets
│   ├── index.html               # Fonts & FontAwesome icons CDN
│   ├── styles.css               # Global banking design system tokens and component styles
│   └── main.ts                  # Application bootstrap
├── angular.json
├── package.json
└── tsconfig.json
```

---

## 🚀 Getting Started

### Prerequisites
- Node.js 18.x or 20.x+
- npm 9.x+

### Installation & Run
```bash
# Install dependencies
npm install

# Start development server (serves on http://localhost:4200)
npm start

# Build for production (outputs to dist/BankEase-Frontend)
npm run build
```

---

## 👥 Demo Logins

| Role | Username | Password | Accessible Features |
|---|---|---|---|
| **Customer** | `rajesh` | `Password@123` | Dashboard, Transfers, Bills, Beneficiaries, Loans, Investments |
| **Bank Staff** | `staff` | `Password@123` | Underwrite Loans, Approve Investments, Customer Accounts |
| **Administrator** | `admin` | `Admin@123` | User Control, Freeze Accounts, Audit Trail, System Metrics |

---

## 🔒 Security Measures

- **JWT Interceptor:** Automatically attaches `Authorization: Bearer <token>` to all protected outgoing requests.
- **Route Guards:** Functional `authGuard` verifies active session; `roleGuard` enforces RBAC (`CUSTOMER`, `BANK_STAFF`, `ADMIN`).
- **Idempotency Headers:** Generates unique idempotency tokens on financial mutations to eliminate duplicate charge hazards.
- **XSS & Form Defense:** Strict typing with Angular Reactive & Template-driven forms, sanitized bindings.
