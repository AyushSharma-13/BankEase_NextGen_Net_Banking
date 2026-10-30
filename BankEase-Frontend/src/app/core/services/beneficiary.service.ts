import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Beneficiary } from '../models/beneficiary.model';

@Injectable({
  providedIn: 'root'
})
export class BeneficiaryService {
  private readonly baseUrl = `${environment.apiUrl}/beneficiaries`;

  constructor(private http: HttpClient) {}

  getBeneficiaries(): Observable<ApiResponse<Beneficiary[]>> {
    return this.http.get<ApiResponse<Beneficiary[]>>(this.baseUrl);
  }

  getBeneficiaryById(id: number): Observable<ApiResponse<Beneficiary>> {
    return this.http.get<ApiResponse<Beneficiary>>(`${this.baseUrl}/${id}`);
  }

  addBeneficiary(data: { name: string; accountNo: string; bankName: string; ifscCode: string }): Observable<ApiResponse<Beneficiary>> {
    return this.http.post<ApiResponse<Beneficiary>>(this.baseUrl, data);
  }

  updateBeneficiary(id: number, data: { name: string; accountNo: string; bankName: string; ifscCode: string }): Observable<ApiResponse<Beneficiary>> {
    return this.http.put<ApiResponse<Beneficiary>>(`${this.baseUrl}/${id}`, data);
  }

  deleteBeneficiary(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }

  validateAccount(accountNo: string): Observable<ApiResponse<boolean>> {
    return this.http.get<ApiResponse<boolean>>(`${this.baseUrl}/validate/${accountNo}`);
  }
}
