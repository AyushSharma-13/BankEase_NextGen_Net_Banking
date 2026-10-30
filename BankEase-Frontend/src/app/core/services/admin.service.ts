import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { SystemMetrics, AuditLog } from '../models/admin.model';
import { UserProfile } from '../models/auth.model';
import { Account } from '../models/account.model';
import { Transaction } from '../models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly baseUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  getMetrics(): Observable<ApiResponse<SystemMetrics>> {
    return this.http.get<ApiResponse<SystemMetrics>>(`${this.baseUrl}/metrics`);
  }

  getUsers(page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<UserProfile>>> {
    return this.http.get<ApiResponse<PageResponse<UserProfile>>>(`${this.baseUrl}/users?page=${page}&size=${size}`);
  }

  toggleUser(userId: number): Observable<ApiResponse<UserProfile>> {
    return this.http.patch<ApiResponse<UserProfile>>(`${this.baseUrl}/users/${userId}/toggle`, {});
  }

  getAccounts(page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Account>>> {
    return this.http.get<ApiResponse<PageResponse<Account>>>(`${this.baseUrl}/accounts?page=${page}&size=${size}`);
  }

  updateAccountStatus(accountId: number, status: string): Observable<ApiResponse<Account>> {
    return this.http.patch<ApiResponse<Account>>(`${this.baseUrl}/accounts/${accountId}/status?status=${status}`, {});
  }

  getAllTransactions(page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Transaction>>> {
    return this.http.get<ApiResponse<PageResponse<Transaction>>>(`${this.baseUrl}/transactions?page=${page}&size=${size}`);
  }

  getAuditLogs(page: number = 0, size: number = 20): Observable<ApiResponse<PageResponse<AuditLog>>> {
    return this.http.get<ApiResponse<PageResponse<AuditLog>>>(`${this.baseUrl}/audit-logs?page=${page}&size=${size}`);
  }

  createStaff(staffData: { username: string; password: string; name: string; email: string; phone: string }): Observable<ApiResponse<UserProfile>> {
    return this.http.post<ApiResponse<UserProfile>>(`${this.baseUrl}/staff`, staffData);
  }
}

