export interface BillPayment {
  billId: number;
  accountId: number;
  accountNo: string;
  category: 'ELECTRICITY' | 'WATER' | 'MOBILE_RECHARGE' | 'INTERNET' | 'GAS';
  billerName: string;
  consumerNumber: string;
  amount: number;
  status: 'PENDING' | 'PROCESSING' | 'PAID' | 'FAILED';
  referenceNo: string;
  createdAt: string;
}

export interface BillPaymentRequest {
  accountId: number;
  category: string;
  billerName: string;
  consumerNumber: string;
  amount: number;
}
