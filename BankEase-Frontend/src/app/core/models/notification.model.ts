export interface Notification {
  notificationId: number;
  title: string;
  message: string;
  type: 'TRANSACTION' | 'SECURITY' | 'LOAN' | 'INVESTMENT' | 'SYSTEM';
  isRead: boolean;
  createdAt: string;
}
