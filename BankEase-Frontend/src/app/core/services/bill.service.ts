import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { BillPayment, BillPaymentRequest } from '../models/bill.model';

@Injectable({
  providedIn: 'root'
})
export class BillService {
  private readonly baseUrl = `${environment.apiUrl}/bills`;

  constructor(private http: HttpClient) {}

  getCategories(): Observable<ApiResponse<string[]>> {
    return this.http.get<ApiResponse<string[]>>(`${this.baseUrl}/categories`);
  }

  payBill(data: BillPaymentRequest): Observable<ApiResponse<BillPayment>> {
    return this.http.post<ApiResponse<BillPayment>>(`${this.baseUrl}/pay`, data);
  }

  getBillHistory(page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<BillPayment>>> {
    return this.http.get<ApiResponse<PageResponse<BillPayment>>>(`${this.baseUrl}/history?page=${page}&size=${size}`);
  }

  getBillPaymentById(id: number): Observable<ApiResponse<BillPayment>> {
    return this.http.get<ApiResponse<BillPayment>>(`${this.baseUrl}/${id}`);
  }
}

