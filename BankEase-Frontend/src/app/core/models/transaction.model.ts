export interface Transaction {
  transactionId: number;
  accountId: number;
  accountNo: string;
  type: 'DEBIT' | 'CREDIT';
  amount: number;
  balanceAfter: number;
  status: 'COMPLETED' | 'PENDING' | 'FAILED';
  description: string;
  referenceNo: string;
  createdAt: string;
}
