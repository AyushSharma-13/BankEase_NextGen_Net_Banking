import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Account, DashboardOverview } from '../models/account.model';
import { Transaction } from '../models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly baseUrl = `${environment.apiUrl}/accounts`;

  constructor(private http: HttpClient) {}

  getAccounts(): Observable<ApiResponse<Account[]>> {
    return this.http.get<ApiResponse<Account[]>>(this.baseUrl);
  }

  getDashboard(): Observable<ApiResponse<DashboardOverview>> {
    return this.http.get<ApiResponse<DashboardOverview>>(`${this.baseUrl}/dashboard`);
  }

  getAccountById(accountId: number): Observable<ApiResponse<Account>> {
    return this.http.get<ApiResponse<Account>>(`${this.baseUrl}/${accountId}`);
  }

  getAccountTransactions(accountId: number, page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Transaction>>> {
    return this.http.get<ApiResponse<PageResponse<Transaction>>>(`${this.baseUrl}/${accountId}/transactions?page=${page}&size=${size}`);
  }

  downloadStatement(accountId: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${accountId}/statement`, {
      responseType: 'blob'
    });
  }
}

