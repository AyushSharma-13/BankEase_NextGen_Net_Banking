import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Transaction } from '../models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private readonly baseUrl = `${environment.apiUrl}/transactions`;

  constructor(private http: HttpClient) {}

  getTransactions(params?: {
    type?: string;
    status?: string;
    startDate?: string;
    endDate?: string;
    search?: string;
    page?: number;
    size?: number;
  }): Observable<ApiResponse<PageResponse<Transaction>>> {
    let httpParams = new HttpParams();
    if (params) {
      if (params.type) httpParams = httpParams.set('type', params.type);
      if (params.status) httpParams = httpParams.set('status', params.status);
      if (params.startDate) httpParams = httpParams.set('startDate', params.startDate);
      if (params.endDate) httpParams = httpParams.set('endDate', params.endDate);
      if (params.search) httpParams = httpParams.set('search', params.search);
      httpParams = httpParams.set('page', (params.page || 0).toString());
      httpParams = httpParams.set('size', (params.size || 10).toString());
    }
    return this.http.get<ApiResponse<PageResponse<Transaction>>>(this.baseUrl, { params: httpParams });
  }

  getTransactionByReference(referenceNo: string): Observable<ApiResponse<Transaction>> {
    return this.http.get<ApiResponse<Transaction>>(`${this.baseUrl}/${referenceNo}`);
  }
}
