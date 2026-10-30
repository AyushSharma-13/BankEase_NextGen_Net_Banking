import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Loan, LoanApplicationRequest, LoanTypeInfo } from '../models/loan.model';

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  private readonly baseUrl = `${environment.apiUrl}/loans`;
  private readonly staffUrl = `${environment.apiUrl}/staff/loans`;

  constructor(private http: HttpClient) {}

  getLoanTypes(): Observable<ApiResponse<LoanTypeInfo[]>> {
    return this.http.get<ApiResponse<LoanTypeInfo[]>>(`${this.baseUrl}/types`);
  }

  applyLoan(data: LoanApplicationRequest): Observable<ApiResponse<Loan>> {
    return this.http.post<ApiResponse<Loan>>(`${this.baseUrl}/apply`, data);
  }

  getMyLoans(page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Loan>>> {
    return this.http.get<ApiResponse<PageResponse<Loan>>>(`${this.baseUrl}?page=${page}&size=${size}`);
  }

  getLoanById(id: number): Observable<ApiResponse<Loan>> {
    return this.http.get<ApiResponse<Loan>>(`${this.baseUrl}/${id}`);
  }

  // Staff and Admin methods
  getAllStaffLoans(page: number = 0, size: number = 20): Observable<ApiResponse<PageResponse<Loan>>> {
    return this.http.get<ApiResponse<PageResponse<Loan>>>(`${this.staffUrl}?page=${page}&size=${size}`);
  }

  updateLoanStatus(loanId: number, status: string, remarks?: string): Observable<ApiResponse<Loan>> {
    return this.http.patch<ApiResponse<Loan>>(`${this.staffUrl}/${loanId}/status`, { status, remarks });
  }
}

