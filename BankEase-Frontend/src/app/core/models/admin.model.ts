export interface SystemMetrics {
  totalCustomers: number;
  totalUsers: number;
  totalAccounts: number;
  totalTransactions: number;
  totalDeposits: number;
  totalLoans: number;
  totalInvestments: number;
}

export interface AuditLog {
  auditId: number;
  username: string;
  action: string;
  endpoint: string;
  ipAddress: string;
  details: string;
  timestamp: string;
}

