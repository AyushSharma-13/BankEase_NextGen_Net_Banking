export interface Investment {
  investmentId: number;
  customerId: number;
  customerName: string;
  investmentType: 'FIXED_DEPOSIT' | 'RECURRING_DEPOSIT' | 'MUTUAL_FUNDS' | 'NATIONAL_PENSION_SCHEME';
  amount: number;
  tenureMonths: number;
  expectedReturnRate: number;
  status: 'REQUESTED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  createdAt: string;
  updatedAt?: string;
}

export interface InvestmentRequest {
  investmentType: string;
  amount: number;
  tenureMonths: number;
}

export interface InvestmentPlanInfo {
  type: string;
  name: string;
  returnRate: number;
  minAmount: number;
  riskLevel: string;
  description: string;
}
