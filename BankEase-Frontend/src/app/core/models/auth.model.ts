export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: number;
  username: string;
  role: 'CUSTOMER' | 'BANK_STAFF' | 'ADMIN';
  customerId?: number;
  customerName: string;
  email: string;
}

export interface UserProfile {
  userId: number;
  customerId?: number;
  username: string;
  role: string;
  name: string;
  email: string;
  phone: string;
  address: string;
  enabled: boolean;
  createdAt: string;
  lastLogin?: string;
}

