export interface Loan {
  loanId: number;
  customerId: number;
  customerName: string;
  loanType: 'HOME' | 'PERSONAL' | 'EDUCATION' | 'VEHICLE';
  amount: number;
  interestRate: number;
  tenureMonths: number;
  monthlyEmi: number;
  status: 'SUBMITTED' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED' | 'CLOSED';
  remarks?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface LoanApplicationRequest {
  loanType: string;
  amount: number;
  tenureMonths: number;
  remarks?: string;
}

export interface LoanTypeInfo {
  type: string;
  name: string;
  interestRate: number;
  minAmount: number;
  maxAmount: number;
  maxTenureMonths: number;
  description: string;
}
