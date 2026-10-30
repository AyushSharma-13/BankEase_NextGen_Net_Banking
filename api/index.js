// Vercel Serverless Function Handler for BankEase APIs
export default function handler(req, res) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, PATCH, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization, Idempotency-Key');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  const { url, method, body } = req;

  // 1. Auth: Login
  if (url.includes('/auth/login') && method === 'POST') {
    const { username, password } = body || {};
    if (username === 'admin' || username === 'staff' || username === 'rajesh' || username === 'priya' || username === 'arun') {
      const role = username === 'admin' ? 'ADMIN' : username === 'staff' ? 'BANK_STAFF' : 'CUSTOMER';
      return res.status(200).json({
        success: true,
        message: 'Authentication successful',
        data: {
          token: 'vercel_jwt_' + Buffer.from(JSON.stringify({ username, role })).toString('base64'),
          tokenType: 'Bearer',
          userId: 1,
          username,
          role,
          customerId: role === 'CUSTOMER' ? 1 : null,
          customerName: username === 'rajesh' ? 'Rajesh Sharma' : username,
          email: `${username}@bankease.com`
        },
        timestamp: new Date().toISOString()
      });
    }
    return res.status(401).json({ success: false, message: 'Invalid credentials', data: null });
  }

  // 2. Auth: Register
  if (url.includes('/auth/register') && method === 'POST') {
    return res.status(200).json({
      success: true,
      message: 'Registration successful',
      data: {
        token: 'vercel_jwt_new',
        tokenType: 'Bearer',
        userId: 99,
        username: body?.username || 'newuser',
        role: 'CUSTOMER',
        customerId: 99,
        customerName: body?.fullName || 'New User',
        email: body?.email || 'user@example.com'
      },
      timestamp: new Date().toISOString()
    });
  }

  // 3. Accounts: Dashboard
  if (url.includes('/accounts/dashboard')) {
    return res.status(200).json({
      success: true,
      message: 'Dashboard data loaded',
      data: {
        customerName: 'Rajesh Sharma',
        email: 'rajesh.sharma@example.com',
        accounts: [
          { accountId: 1, customerId: 1, accountNo: '100023456781', accountType: 'SAVINGS', balance: 154250.00, status: 'ACTIVE' },
          { accountId: 2, customerId: 1, accountNo: '100023456782', accountType: 'CURRENT', balance: 48500.00, status: 'ACTIVE' }
        ],
        totalBalance: 202750.00,
        recentTransactions: [
          { transactionId: 1, accountNo: '100023456781', type: 'CREDIT', amount: 75000, status: 'COMPLETED', description: 'Salary Credit - TechCorp', referenceNo: 'TXN-001', createdAt: new Date().toISOString() }
        ],
        unreadNotificationsCount: 2,
        totalBeneficiariesCount: 2,
        activeLoansCount: 1,
        activeInvestmentsCount: 2
      },
      timestamp: new Date().toISOString()
    });
  }

  // Default fallback response
  return res.status(200).json({
    success: true,
    message: 'Operation completed successfully',
    data: {},
    timestamp: new Date().toISOString()
  });
}
