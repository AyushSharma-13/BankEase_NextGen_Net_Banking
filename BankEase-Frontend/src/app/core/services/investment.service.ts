import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Investment, InvestmentRequest, InvestmentPlanInfo } from '../models/investment.model';

@Injectable({
  providedIn: 'root'
})
export class InvestmentService {
  private readonly baseUrl = `${environment.apiUrl}/investments`;
  private readonly staffUrl = `${environment.apiUrl}/staff/investments`;

  constructor(private http: HttpClient) {}

  getInvestmentPlans(): Observable<ApiResponse<InvestmentPlanInfo[]>> {
    return this.http.get<ApiResponse<InvestmentPlanInfo[]>>(`${this.baseUrl}/plans`);
  }

  applyInvestment(data: InvestmentRequest): Observable<ApiResponse<Investment>> {
    return this.http.post<ApiResponse<Investment>>(this.baseUrl, data);
  }

  getMyInvestments(page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<Investment>>> {
    return this.http.get<ApiResponse<PageResponse<Investment>>>(`${this.baseUrl}?page=${page}&size=${size}`);
  }

  getInvestmentById(id: number): Observable<ApiResponse<Investment>> {
    return this.http.get<ApiResponse<Investment>>(`${this.baseUrl}/${id}`);
  }

  // Staff and Admin methods
  getAllStaffInvestments(page: number = 0, size: number = 20): Observable<ApiResponse<PageResponse<Investment>>> {
    return this.http.get<ApiResponse<PageResponse<Investment>>>(`${this.staffUrl}?page=${page}&size=${size}`);
  }

  updateInvestmentStatus(id: number, status: string, remarks?: string): Observable<ApiResponse<Investment>> {
    return this.http.patch<ApiResponse<Investment>>(`${this.staffUrl}/${id}/status`, { status, remarks });
  }
}

