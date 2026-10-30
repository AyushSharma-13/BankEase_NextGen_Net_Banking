export interface TransferRequest {
  sourceAccountId: number;
  destinationAccountNo: string;
  beneficiaryId?: number;
  amount: number;
  remarks?: string;
  otpCode: string;
  idempotencyKey?: string;
}

export interface TransferResponse {
  transferId: number;
  referenceNo: string;
  sourceAccountNo: string;
  destinationAccountNo: string;
  amount: number;
  status: string;
  remarks?: string;
  timestamp: string;
}

