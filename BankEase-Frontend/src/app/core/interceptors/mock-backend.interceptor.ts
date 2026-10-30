import { HttpInterceptorFn, HttpResponse, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { DemoDataService } from '../services/demo-data.service';

export const mockBackendInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const demoData = inject(DemoDataService);

  // First try the real backend. If it fails (e.g. status 0, 404 on Vercel), fall back to in-memory demo backend.
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // If server unreachable, 404, or returned HTML instead of JSON
      if (error.status === 0 || error.status === 404 || error.status === 502 || error.status === 504 || (error.error instanceof ProgressEvent) || (typeof error.error === 'string' && error.error.includes('<!DOCTYPE html>'))) {
        const handled = handleMockRequest(req, demoData);
        if (handled) {
          return of(handled);
        }
      }
      return throwError(() => error);
    })
  );
};

function handleMockRequest(req: HttpRequest<unknown>, demo: DemoDataService): HttpResponse<any> | null {
  const url = req.url;
  const method = req.method;
  const body = req.body as any;

  // 1. AUTH: Login
  if (url.includes('/api/auth/login') && method === 'POST') {
    const users = demo.getItem<any[]>('users');
    const user = users.find(u => u.username === body?.username && (u.password === body?.password || body?.password === 'Password@123' || body?.password === 'Admin@123'));
    if (!user) {
      return new HttpResponse({
        status: 401,
        body: { success: false, message: 'Invalid username or password', data: null, timestamp: new Date().toISOString() }
      });
    }
    const token = 'mock_jwt_token_' + btoa(JSON.stringify({ userId: user.userId, username: user.username, role: user.role }));
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Authentication successful',
        data: {
          token,
          tokenType: 'Bearer',
          userId: user.userId,
          username: user.username,
          role: user.role,
          customerId: user.customerId,
          customerName: user.name,
          email: user.email
        },
        timestamp: new Date().toISOString()
      }
    });
  }

  // 2. AUTH: Register
  if (url.includes('/api/auth/register') && method === 'POST') {
    const users = demo.getItem<any[]>('users');
    const newUserId = users.length + 1;
    const newCustId = users.length + 1;
    const newUser = {
      userId: newUserId,
      customerId: newCustId,
      username: body.username,
      name: body.fullName || body.name,
      email: body.email,
      phone: body.phone,
      address: body.address,
      panNumber: body.panNumber,
      role: 'CUSTOMER',
      enabled: true,
      password: body.password || 'Password@123',
      createdAt: new Date().toISOString()
    };
    users.push(newUser);
    demo.setItem('users', users);

    // Create opening savings account
    const accounts = demo.getItem<any[]>('accounts');
    const newAcc = {
      accountId: accounts.length + 1,
      customerId: newCustId,
      accountNo: '1000' + Math.floor(10000000 + Math.random() * 90000000),
      accountType: 'SAVINGS',
      balance: body.initialDeposit || 10000,
      status: 'ACTIVE',
      customerName: newUser.name,
      createdAt: new Date().toISOString()
    };
    accounts.push(newAcc);
    demo.setItem('accounts', accounts);

    const token = 'mock_jwt_token_' + btoa(JSON.stringify({ userId: newUserId, username: newUser.username, role: 'CUSTOMER' }));
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Registration successful! Savings account created.',
        data: {
          token,
          tokenType: 'Bearer',
          userId: newUserId,
          username: newUser.username,
          role: 'CUSTOMER',
          customerId: newCustId,
          customerName: newUser.name,
          email: newUser.email
        },
        timestamp: new Date().toISOString()
      }
    });
  }

  // 3. AUTH: Profile / Me
  if (url.includes('/api/auth/me') && method === 'GET') {
    const users = demo.getItem<any[]>('users');
    const user = users[0]; // Default to Rajesh
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Profile retrieved', data: user, timestamp: new Date().toISOString() }
    });
  }

  // 4. AUTH: Change Password
  if (url.includes('/api/auth/change-password') && method === 'POST') {
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Password changed successfully', data: null, timestamp: new Date().toISOString() }
    });
  }

  // 5. ACCOUNTS: Dashboard
  if (url.includes('/api/accounts/dashboard') && method === 'GET') {
    const accounts = demo.getItem<any[]>('accounts').filter(a => a.customerId === 1);
    const transactions = demo.getItem<any[]>('transactions');
    const totalBalance = accounts.reduce((acc, curr) => acc + (Number(curr.balance) || 0), 0);
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Dashboard loaded',
        data: {
          customerName: 'Rajesh Sharma',
          email: 'rajesh.sharma@example.com',
          accounts,
          totalBalance,
          recentTransactions: transactions.slice(0, 5),
          unreadNotificationsCount: 2,
          totalBeneficiariesCount: 2,
          activeLoansCount: 1,
          activeInvestmentsCount: 2
        },
        timestamp: new Date().toISOString()
      }
    });
  }

  // 6. ACCOUNTS: List
  if ((url.endsWith('/api/accounts') || url.includes('/api/accounts?')) && method === 'GET') {
    const accounts = demo.getItem<any[]>('accounts').filter(a => a.customerId === 1);
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Accounts retrieved', data: accounts, timestamp: new Date().toISOString() }
    });
  }

  // 7. ACCOUNTS: Statement
  if (url.includes('/statement') && method === 'GET') {
    const csvContent = "Transaction ID,Date,Type,Amount,Status,Description\n1,2026-10-01,CREDIT,100000,COMPLETED,Opening Deposit\n2,2026-10-15,CREDIT,75000,COMPLETED,Salary\n";
    return new HttpResponse({
      status: 200,
      body: new Blob([csvContent], { type: 'text/csv' })
    });
  }

  // 8. BENEFICIARIES: List
  if ((url.endsWith('/api/beneficiaries') || url.includes('/api/beneficiaries?')) && method === 'GET') {
    const beneficiaries = demo.getItem<any[]>('beneficiaries');
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Beneficiaries retrieved', data: beneficiaries, timestamp: new Date().toISOString() }
    });
  }

  // 9. BENEFICIARIES: Add
  if (url.includes('/api/beneficiaries') && method === 'POST') {
    const beneficiaries = demo.getItem<any[]>('beneficiaries');
    const newBen = {
      beneficiaryId: beneficiaries.length + 1,
      customerId: 1,
      ...body,
      createdAt: new Date().toISOString()
    };
    beneficiaries.push(newBen);
    demo.setItem('beneficiaries', beneficiaries);
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Beneficiary added successfully', data: newBen, timestamp: new Date().toISOString() }
    });
  }

  // 10. BENEFICIARIES: Delete
  if (url.includes('/api/beneficiaries/') && method === 'DELETE') {
    const parts = url.split('/');
    const id = parseInt(parts[parts.length - 1], 10);
    let beneficiaries = demo.getItem<any[]>('beneficiaries');
    beneficiaries = beneficiaries.filter(b => b.beneficiaryId !== id);
    demo.setItem('beneficiaries', beneficiaries);
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Beneficiary deleted successfully', data: null, timestamp: new Date().toISOString() }
    });
  }

  // 11. TRANSFERS: Send OTP
  if (url.includes('/api/transfers/send-otp') && method === 'POST') {
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'OTP sent: 123456', data: '123456', timestamp: new Date().toISOString() }
    });
  }

  // 12. TRANSFERS: Execute Transfer
  if (url.includes('/api/transfers') && method === 'POST') {
    const accounts = demo.getItem<any[]>('accounts');
    const transactions = demo.getItem<any[]>('transactions');
    const srcAcc = accounts.find(a => a.accountNo === body.sourceAccountNo || a.accountId === Number(body.sourceAccountNo));
    
    if (srcAcc && srcAcc.balance < body.amount) {
      return new HttpResponse({
        status: 400,
        body: { success: false, message: 'Insufficient balance in source account', data: null, timestamp: new Date().toISOString() }
      });
    }

    if (srcAcc) {
      srcAcc.balance = Number(srcAcc.balance) - Number(body.amount);
      demo.setItem('accounts', accounts);
    }

    const ref = 'TXN-BEAS-' + Math.floor(100000 + Math.random() * 900000);
    const newTxn = {
      transactionId: transactions.length + 1,
      accountId: srcAcc?.accountId || 1,
      accountNo: srcAcc?.accountNo || '100023456781',
      type: 'DEBIT',
      amount: body.amount,
      balanceAfter: srcAcc?.balance || 149250,
      status: 'COMPLETED',
      description: 'Transfer to ' + (body.beneficiaryName || 'Beneficiary'),
      referenceNo: ref,
      createdAt: new Date().toISOString()
    };
    transactions.unshift(newTxn);
    demo.setItem('transactions', transactions);

    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Fund transfer executed successfully',
        data: {
          transferId: Math.floor(1000 + Math.random() * 9000),
          referenceNumber: ref,
          amount: body.amount,
          sourceAccountNo: srcAcc?.accountNo || '100023456781',
          destinationAccountNo: body.destinationAccountNo,
          beneficiaryName: body.beneficiaryName || 'Beneficiary',
          status: 'COMPLETED',
          timestamp: new Date().toISOString()
        },
        timestamp: new Date().toISOString()
      }
    });
  }

  // 13. TRANSACTIONS: List
  if (url.includes('/api/transactions') && method === 'GET') {
    const transactions = demo.getItem<any[]>('transactions');
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Transactions retrieved',
        data: {
          content: transactions,
          page: 0,
          size: 20,
          totalElements: transactions.length,
          totalPages: 1
        },
        timestamp: new Date().toISOString()
      }
    });
  }

  // 14. BILLS: Pay Bill
  if (url.includes('/api/bills/pay') && method === 'POST') {
    const accounts = demo.getItem<any[]>('accounts');
    const transactions = demo.getItem<any[]>('transactions');
    const acc = accounts[0];
    if (acc) {
      acc.balance = Number(acc.balance) - Number(body.amount);
      demo.setItem('accounts', accounts);
    }
    const ref = 'BILL-BEAS-' + Math.floor(100000 + Math.random() * 900000);
    transactions.unshift({
      transactionId: transactions.length + 1,
      accountId: acc?.accountId || 1,
      accountNo: acc?.accountNo || '100023456781',
      type: 'DEBIT',
      amount: body.amount,
      balanceAfter: acc?.balance || 150000,
      status: 'COMPLETED',
      description: 'Utility Bill Payment - ' + body.billerName,
      referenceNo: ref,
      createdAt: new Date().toISOString()
    });
    demo.setItem('transactions', transactions);

    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Bill payment processed successfully',
        data: {
          paymentId: Math.floor(100 + Math.random() * 900),
          billerCategory: body.category,
          billerName: body.billerName,
          consumerNumber: body.consumerNumber,
          amount: body.amount,
          receiptNumber: ref,
          status: 'COMPLETED',
          paymentDate: new Date().toISOString()
        },
        timestamp: new Date().toISOString()
      }
    });
  }

  // 15. LOANS: Types
  if (url.includes('/api/loans/types') && method === 'GET') {
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Loan types retrieved',
        data: [
          { type: 'HOME', name: 'Home Loan', interestRate: 8.50, minAmount: 500000, maxAmount: 10000000, maxTenureMonths: 360, description: 'Affordable home financing with minimal documentation.' },
          { type: 'PERSONAL', name: 'Personal Loan', interestRate: 11.50, minAmount: 50000, maxAmount: 1500000, maxTenureMonths: 60, description: 'Quick personal credit for travel, medical, or emergencies.' },
          { type: 'EDUCATION', name: 'Education Loan', interestRate: 9.00, minAmount: 100000, maxAmount: 5000000, maxTenureMonths: 180, description: 'Student loans covering tuition and overseas living costs.' },
          { type: 'VEHICLE', name: 'Vehicle Loan', interestRate: 8.90, minAmount: 100000, maxAmount: 3000000, maxTenureMonths: 84, description: 'Attractive interest rates for new cars and two-wheelers.' }
        ],
        timestamp: new Date().toISOString()
      }
    });
  }

  // 16. LOANS: List & Apply
  if (url.includes('/api/loans') && method === 'GET') {
    const loans = demo.getItem<any[]>('loans');
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Loans retrieved',
        data: { content: loans, totalElements: loans.length, totalPages: 1 },
        timestamp: new Date().toISOString()
      }
    });
  }

  if (url.includes('/api/loans') && method === 'POST') {
    const loans = demo.getItem<any[]>('loans');
    const newLoan = {
      loanId: loans.length + 1,
      customerId: 1,
      customerName: 'Rajesh Sharma',
      loanType: body.loanType,
      amount: body.amount,
      interestRate: 8.5,
      tenureMonths: body.tenureMonths,
      monthlyEmi: Math.round((body.amount * 0.085 / 12) * 1.15),
      status: 'SUBMITTED',
      remarks: body.remarks || 'Application under initial review',
      createdAt: new Date().toISOString()
    };
    loans.push(newLoan);
    demo.setItem('loans', loans);
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Loan application submitted successfully!', data: newLoan, timestamp: new Date().toISOString() }
    });
  }

  // 17. INVESTMENTS: Plans
  if (url.includes('/api/investments/plans') && method === 'GET') {
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Investment plans retrieved',
        data: [
          { type: 'FIXED_DEPOSIT', name: 'Fixed Deposit (FD)', returnRate: 7.25, minAmount: 10000, riskLevel: 'Low Risk', description: 'Guaranteed returns with sovereign-grade capital safety.' },
          { type: 'RECURRING_DEPOSIT', name: 'Recurring Deposit (RD)', returnRate: 6.80, minAmount: 1000, riskLevel: 'Low Risk', description: 'Systematic monthly savings with guaranteed compounded interest.' },
          { type: 'MUTUAL_FUNDS', name: 'Bluechip Equity Mutual Fund', returnRate: 12.50, minAmount: 5000, riskLevel: 'Moderate Risk', description: 'Market-linked diversified equity portfolio for wealth creation.' },
          { type: 'NATIONAL_PENSION_SCHEME', name: 'National Pension Scheme (NPS)', returnRate: 9.80, minAmount: 5000, riskLevel: 'Low to Moderate', description: 'Government-backed retirement plan with triple tax exemptions.' }
        ],
        timestamp: new Date().toISOString()
      }
    });
  }

  // 18. INVESTMENTS: List & Apply
  if (url.includes('/api/investments') && method === 'GET') {
    const investments = demo.getItem<any[]>('investments');
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Investments retrieved',
        data: { content: investments, totalElements: investments.length, totalPages: 1 },
        timestamp: new Date().toISOString()
      }
    });
  }

  if (url.includes('/api/investments') && method === 'POST') {
    const investments = demo.getItem<any[]>('investments');
    const newInv = {
      investmentId: investments.length + 1,
      customerId: 1,
      customerName: 'Rajesh Sharma',
      investmentType: body.investmentType,
      amount: body.amount,
      tenureMonths: body.tenureMonths,
      expectedReturnRate: 7.5,
      status: 'REQUESTED',
      createdAt: new Date().toISOString()
    };
    investments.push(newInv);
    demo.setItem('investments', investments);
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Investment request submitted successfully!', data: newInv, timestamp: new Date().toISOString() }
    });
  }

  // 19. NOTIFICATIONS: List & Read
  if (url.includes('/api/notifications') && method === 'GET') {
    const notifications = demo.getItem<any[]>('notifications');
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Notifications retrieved',
        data: { content: notifications, totalElements: notifications.length, totalPages: 1 },
        timestamp: new Date().toISOString()
      }
    });
  }

  if (url.includes('/api/notifications/read-all') || url.includes('/read')) {
    const notifications = demo.getItem<any[]>('notifications');
    notifications.forEach(n => n.isRead = true);
    demo.setItem('notifications', notifications);
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Notifications marked as read', data: null, timestamp: new Date().toISOString() }
    });
  }

  // 20. ADMIN: Metrics, Users, Accounts, Audit
  if (url.includes('/api/admin/metrics') && method === 'GET') {
    const accounts = demo.getItem<any[]>('accounts');
    const users = demo.getItem<any[]>('users');
    const totalDeposits = accounts.reduce((acc, curr) => acc + (Number(curr.balance) || 0), 0);
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Metrics retrieved',
        data: {
          totalUsers: users.length,
          totalAccounts: accounts.length,
          totalCustomers: 3,
          totalDeposits: totalDeposits,
          totalTransactions: 24,
          totalLoans: 1,
          totalInvestments: 2
        },
        timestamp: new Date().toISOString()
      }
    });
  }

  if (url.includes('/api/admin/users') && method === 'GET') {
    const users = demo.getItem<any[]>('users');
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Users retrieved',
        data: { content: users, totalElements: users.length, totalPages: 1 },
        timestamp: new Date().toISOString()
      }
    });
  }

  if (url.includes('/api/admin/users/') && url.includes('/toggle')) {
    const parts = url.split('/');
    const id = parseInt(parts[parts.indexOf('users') + 1], 10);
    const users = demo.getItem<any[]>('users');
    const u = users.find(x => x.userId === id);
    if (u) {
      u.enabled = !u.enabled;
      demo.setItem('users', users);
    }
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'User status updated', data: u, timestamp: new Date().toISOString() }
    });
  }

  if (url.includes('/api/admin/accounts') && method === 'GET') {
    const accounts = demo.getItem<any[]>('accounts');
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Accounts retrieved',
        data: { content: accounts, totalElements: accounts.length, totalPages: 1 },
        timestamp: new Date().toISOString()
      }
    });
  }

  if (url.includes('/api/admin/accounts/') && url.includes('/status')) {
    const parts = url.split('/');
    const id = parseInt(parts[parts.indexOf('accounts') + 1], 10);
    const status = new URL(url, 'http://localhost').searchParams.get('status') || 'ACTIVE';
    const accounts = demo.getItem<any[]>('accounts');
    const acc = accounts.find(a => a.accountId === id);
    if (acc) {
      acc.status = status;
      demo.setItem('accounts', accounts);
    }
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Account status updated', data: acc, timestamp: new Date().toISOString() }
    });
  }

  if (url.includes('/api/admin/audit-logs') && method === 'GET') {
    const logs = demo.getItem<any[]>('audit_logs');
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Audit logs retrieved',
        data: { content: logs, totalElements: logs.length, totalPages: 1 },
        timestamp: new Date().toISOString()
      }
    });
  }

  // 21. STAFF: Loans & Investments
  if (url.includes('/api/staff/loans') && method === 'GET') {
    const loans = demo.getItem<any[]>('loans');
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Staff loans retrieved',
        data: { content: loans, totalElements: loans.length, totalPages: 1 },
        timestamp: new Date().toISOString()
      }
    });
  }

  if (url.includes('/api/staff/loans/') && method === 'PATCH') {
    const parts = url.split('/');
    const id = parseInt(parts[parts.indexOf('loans') + 1], 10);
    const loans = demo.getItem<any[]>('loans');
    const loan = loans.find(l => l.loanId === id);
    if (loan) {
      loan.status = body.status;
      loan.remarks = body.remarks;
      demo.setItem('loans', loans);
    }
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Loan status updated', data: loan, timestamp: new Date().toISOString() }
    });
  }

  if (url.includes('/api/staff/investments') && method === 'GET') {
    const investments = demo.getItem<any[]>('investments');
    return new HttpResponse({
      status: 200,
      body: {
        success: true,
        message: 'Staff investments retrieved',
        data: { content: investments, totalElements: investments.length, totalPages: 1 },
        timestamp: new Date().toISOString()
      }
    });
  }

  if (url.includes('/api/staff/investments/') && method === 'PATCH') {
    const parts = url.split('/');
    const id = parseInt(parts[parts.indexOf('investments') + 1], 10);
    const investments = demo.getItem<any[]>('investments');
    const inv = investments.find(i => i.investmentId === id);
    if (inv) {
      inv.status = body.status;
      demo.setItem('investments', investments);
    }
    return new HttpResponse({
      status: 200,
      body: { success: true, message: 'Investment status updated', data: inv, timestamp: new Date().toISOString() }
    });
  }

  return null;
}
