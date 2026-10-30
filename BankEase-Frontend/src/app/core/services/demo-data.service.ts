import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DemoDataService {
  private readonly STORAGE_PREFIX = 'bankease_mock_';

  constructor() {
    this.initStorage();
  }

  private initStorage(): void {
    if (!localStorage.getItem(`${this.STORAGE_PREFIX}users`)) {
      const users = [
        {
          userId: 1,
          customerId: 1,
          username: 'rajesh',
          name: 'Rajesh Sharma',
          email: 'rajesh.sharma@example.com',
          phone: '+91 98765 43210',
          address: '42 MG Road, Bangalore, Karnataka - 560001',
          panNumber: 'ABCDE1234F',
          role: 'CUSTOMER',
          enabled: true,
          password: 'Password@123',
          createdAt: new Date(Date.now() - 90 * 86400000).toISOString()
        },
        {
          userId: 2,
          customerId: 2,
          username: 'priya',
          name: 'Priya Patel',
          email: 'priya.patel@example.com',
          phone: '+91 98234 56789',
          address: '15 Nehru Place, New Delhi - 110019',
          panNumber: 'FGHIJ5678K',
          role: 'CUSTOMER',
          enabled: true,
          password: 'Password@123',
          createdAt: new Date(Date.now() - 60 * 86400000).toISOString()
        },
        {
          userId: 3,
          customerId: 3,
          username: 'arun',
          name: 'Arun Kumar',
          email: 'arun.kumar@example.com',
          phone: '+91 97123 45678',
          address: '88 Anna Salai, Chennai, Tamil Nadu - 600002',
          panNumber: 'KLMNO9012P',
          role: 'CUSTOMER',
          enabled: true,
          password: 'Password@123',
          createdAt: new Date(Date.now() - 45 * 86400000).toISOString()
        },
        {
          userId: 4,
          username: 'staff',
          name: 'Bank Staff Amit Verma',
          email: 'amit.verma@bankease.internal',
          phone: '+91 99999 11111',
          role: 'BANK_STAFF',
          enabled: true,
          password: 'Password@123',
          createdAt: new Date(Date.now() - 120 * 86400000).toISOString()
        },
        {
          userId: 5,
          username: 'admin',
          name: 'System Administrator',
          email: 'admin@bankease.internal',
          phone: '+91 99999 00000',
          role: 'ADMIN',
          enabled: true,
          password: 'Admin@123',
          createdAt: new Date(Date.now() - 180 * 86400000).toISOString()
        }
      ];
      localStorage.setItem(`${this.STORAGE_PREFIX}users`, JSON.stringify(users));
    }

    if (!localStorage.getItem(`${this.STORAGE_PREFIX}accounts`)) {
      const accounts = [
        {
          accountId: 1,
          customerId: 1,
          accountNo: '100023456781',
          accountType: 'SAVINGS',
          balance: 154250.00,
          status: 'ACTIVE',
          customerName: 'Rajesh Sharma',
          createdAt: new Date(Date.now() - 90 * 86400000).toISOString()
        },
        {
          accountId: 2,
          customerId: 1,
          accountNo: '100023456782',
          accountType: 'CURRENT',
          balance: 48500.00,
          status: 'ACTIVE',
          customerName: 'Rajesh Sharma',
          createdAt: new Date(Date.now() - 80 * 86400000).toISOString()
        },
        {
          accountId: 3,
          customerId: 2,
          accountNo: '100023456783',
          accountType: 'SAVINGS',
          balance: 85200.00,
          status: 'ACTIVE',
          customerName: 'Priya Patel',
          createdAt: new Date(Date.now() - 60 * 86400000).toISOString()
        },
        {
          accountId: 4,
          customerId: 3,
          accountNo: '100023456784',
          accountType: 'SAVINGS',
          balance: 112000.00,
          status: 'ACTIVE',
          customerName: 'Arun Kumar',
          createdAt: new Date(Date.now() - 45 * 86400000).toISOString()
        }
      ];
      localStorage.setItem(`${this.STORAGE_PREFIX}accounts`, JSON.stringify(accounts));
    }

    if (!localStorage.getItem(`${this.STORAGE_PREFIX}transactions`)) {
      const transactions = [
        {
          transactionId: 1,
          accountId: 1,
          accountNo: '100023456781',
          type: 'CREDIT',
          amount: 100000.00,
          balanceAfter: 100000.00,
          status: 'COMPLETED',
          description: 'Initial Opening Deposit',
          referenceNo: 'TXN-BEAS-SEED-001',
          createdAt: new Date(Date.now() - 85 * 86400000).toISOString()
        },
        {
          transactionId: 2,
          accountId: 1,
          accountNo: '100023456781',
          type: 'CREDIT',
          amount: 75000.00,
          balanceAfter: 175000.00,
          status: 'COMPLETED',
          description: 'Salary Credit - TechCorp Global',
          referenceNo: 'TXN-BEAS-SEED-002',
          createdAt: new Date(Date.now() - 15 * 86400000).toISOString()
        },
        {
          transactionId: 3,
          accountId: 1,
          accountNo: '100023456781',
          type: 'DEBIT',
          amount: 15000.00,
          balanceAfter: 160000.00,
          status: 'COMPLETED',
          description: 'Transfer to Priya Patel',
          referenceNo: 'TXN-BEAS-SEED-003',
          createdAt: new Date(Date.now() - 5 * 86400000).toISOString()
        },
        {
          transactionId: 4,
          accountId: 1,
          accountNo: '100023456781',
          type: 'DEBIT',
          amount: 3250.00,
          balanceAfter: 156750.00,
          status: 'COMPLETED',
          description: 'Electricity Bill - BESCOM Bangalore',
          referenceNo: 'TXN-BEAS-SEED-004',
          createdAt: new Date(Date.now() - 2 * 86400000).toISOString()
        },
        {
          transactionId: 5,
          accountId: 1,
          accountNo: '100023456781',
          type: 'DEBIT',
          amount: 2500.00,
          balanceAfter: 154250.00,
          status: 'COMPLETED',
          description: 'Internet Fiber Bill - Airtel Broadband',
          referenceNo: 'TXN-BEAS-SEED-005',
          createdAt: new Date(Date.now() - 1 * 86400000).toISOString()
        }
      ];
      localStorage.setItem(`${this.STORAGE_PREFIX}transactions`, JSON.stringify(transactions));
    }

    if (!localStorage.getItem(`${this.STORAGE_PREFIX}beneficiaries`)) {
      const beneficiaries = [
        {
          beneficiaryId: 1,
          customerId: 1,
          beneficiaryName: 'Priya Patel',
          accountNumber: '100023456783',
          bankName: 'BankEase Main Branch',
          ifscCode: 'BEAS0001001',
          accountType: 'SAVINGS',
          email: 'priya.patel@example.com',
          phone: '+91 98234 56789',
          createdAt: new Date(Date.now() - 30 * 86400000).toISOString()
        },
        {
          beneficiaryId: 2,
          customerId: 1,
          beneficiaryName: 'Arun Kumar',
          accountNumber: '100023456784',
          bankName: 'BankEase Chennai Central',
          ifscCode: 'BEAS0001003',
          accountType: 'SAVINGS',
          email: 'arun.kumar@example.com',
          phone: '+91 97123 45678',
          createdAt: new Date(Date.now() - 25 * 86400000).toISOString()
        }
      ];
      localStorage.setItem(`${this.STORAGE_PREFIX}beneficiaries`, JSON.stringify(beneficiaries));
    }

    if (!localStorage.getItem(`${this.STORAGE_PREFIX}loans`)) {
      const loans = [
        {
          loanId: 1,
          customerId: 1,
          customerName: 'Rajesh Sharma',
          loanType: 'HOME',
          amount: 3500000.00,
          interestRate: 8.5,
          tenureMonths: 240,
          monthlyEmi: 30378.00,
          status: 'UNDER_REVIEW',
          remarks: 'KYC verified, property document appraisal pending.',
          createdAt: new Date(Date.now() - 7 * 86400000).toISOString()
        }
      ];
      localStorage.setItem(`${this.STORAGE_PREFIX}loans`, JSON.stringify(loans));
    }

    if (!localStorage.getItem(`${this.STORAGE_PREFIX}investments`)) {
      const investments = [
        {
          investmentId: 1,
          customerId: 1,
          customerName: 'Rajesh Sharma',
          investmentType: 'FIXED_DEPOSIT',
          amount: 100000.00,
          tenureMonths: 12,
          expectedReturnRate: 7.25,
          status: 'ACTIVE',
          createdAt: new Date(Date.now() - 60 * 86400000).toISOString()
        },
        {
          investmentId: 2,
          customerId: 1,
          customerName: 'Rajesh Sharma',
          investmentType: 'MUTUAL_FUNDS',
          amount: 50000.00,
          tenureMonths: 36,
          expectedReturnRate: 12.50,
          status: 'ACTIVE',
          createdAt: new Date(Date.now() - 20 * 86400000).toISOString()
        }
      ];
      localStorage.setItem(`${this.STORAGE_PREFIX}investments`, JSON.stringify(investments));
    }

    if (!localStorage.getItem(`${this.STORAGE_PREFIX}notifications`)) {
      const notifications = [
        {
          notificationId: 1,
          customerId: 1,
          title: 'Welcome to BankEase NextGen Banking',
          message: 'Your savings account 100023456781 has been opened with full digital banking privileges.',
          type: 'SECURITY',
          isRead: true,
          createdAt: new Date(Date.now() - 90 * 86400000).toISOString()
        },
        {
          notificationId: 2,
          customerId: 1,
          title: 'Home Loan Application Under Review',
          message: 'Your application for a Home Loan of Rs. 35,00,000.00 is currently under review by our credit verification team.',
          type: 'LOAN',
          isRead: false,
          createdAt: new Date(Date.now() - 7 * 86400000).toISOString()
        },
        {
          notificationId: 3,
          customerId: 1,
          title: 'Bill Payment Successful',
          message: 'Electricity bill of Rs. 3,250.00 paid to BESCOM Bangalore. Ref: BILL-BEAS-SEED-001',
          type: 'TRANSACTION',
          isRead: false,
          createdAt: new Date(Date.now() - 2 * 86400000).toISOString()
        }
      ];
      localStorage.setItem(`${this.STORAGE_PREFIX}notifications`, JSON.stringify(notifications));
    }

    if (!localStorage.getItem(`${this.STORAGE_PREFIX}audit_logs`)) {
      const logs = [
        {
          auditId: 1,
          username: 'rajesh',
          action: 'USER_LOGIN',
          endpoint: '/api/auth/login',
          ipAddress: '127.0.0.1',
          details: 'Successful user authentication',
          timestamp: new Date(Date.now() - 2 * 3600000).toISOString()
        },
        {
          auditId: 2,
          username: 'rajesh',
          action: 'FUND_TRANSFER',
          endpoint: '/api/transfers',
          ipAddress: '127.0.0.1',
          details: 'Transfer of Rs. 15,000 to Priya Patel',
          timestamp: new Date(Date.now() - 5 * 86400000).toISOString()
        },
        {
          auditId: 3,
          username: 'admin',
          action: 'ADMIN_ACCESS',
          endpoint: '/api/admin/metrics',
          ipAddress: '127.0.0.1',
          details: 'Loaded system telemetry metrics',
          timestamp: new Date(Date.now() - 1 * 3600000).toISOString()
        }
      ];
      localStorage.setItem(`${this.STORAGE_PREFIX}audit_logs`, JSON.stringify(logs));
    }
  }

  getItem<T>(key: string): T {
    const raw = localStorage.getItem(`${this.STORAGE_PREFIX}${key}`);
    return raw ? JSON.parse(raw) : ([] as unknown as T);
  }

  setItem<T>(key: string, value: T): void {
    localStorage.setItem(`${this.STORAGE_PREFIX}${key}`, JSON.stringify(value));
  }
}
