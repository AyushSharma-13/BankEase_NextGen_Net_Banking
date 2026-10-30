import { Transaction } from './transaction.model';

export interface Account {
  accountId: number;
  customerId: number;
  accountNo: string;
  accountNumber?: string;
  customerName?: string;
  accountType: 'SAVINGS' | 'CURRENT' | 'SALARY' | string;
  balance: number;
  status: 'ACTIVE' | 'FROZEN' | 'CLOSED' | string;
  createdAt: string;
}

export interface DashboardOverview {
  customerName: string;
  email: string;
  accounts: Account[];
  totalBalance: number;
  recentTransactions: Transaction[];
  unreadNotificationsCount: number;
  totalBeneficiariesCount: number;
  activeLoansCount: number;
  activeInvestmentsCount: number;
}
