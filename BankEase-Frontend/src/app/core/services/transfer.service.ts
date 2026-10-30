import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { TransferRequest, TransferResponse } from '../models/transfer.model';

@Injectable({
  providedIn: 'root'
})
export class TransferService {
  private readonly baseUrl = `${environment.apiUrl}/transfers`;

  constructor(private http: HttpClient) {}

  requestOtp(): Observable<ApiResponse<{ otpCode: string; expiresInMinutes: string }>> {
    return this.http.post<ApiResponse<{ otpCode: string; expiresInMinutes: string }>>(`${this.baseUrl}/otp`, {});
  }

  processTransfer(data: TransferRequest): Observable<ApiResponse<TransferResponse>> {
    return this.http.post<ApiResponse<TransferResponse>>(this.baseUrl, data);
  }

  getTransferById(id: number): Observable<ApiResponse<TransferResponse>> {
    return this.http.get<ApiResponse<TransferResponse>>(`${this.baseUrl}/${id}`);
  }

  getTransferHistory(page: number = 0, size: number = 10): Observable<ApiResponse<PageResponse<TransferResponse>>> {
    return this.http.get<ApiResponse<PageResponse<TransferResponse>>>(`${this.baseUrl}?page=${page}&size=${size}`);
  }
}

