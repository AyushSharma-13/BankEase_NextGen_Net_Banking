export interface Beneficiary {
  beneficiaryId: number;
  customerId: number;
  name: string;
  accountNo: string;
  bankName: string;
  ifscCode: string;
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
}

