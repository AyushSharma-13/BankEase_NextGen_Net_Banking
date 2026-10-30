import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Notification } from '../models/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly baseUrl = `${environment.apiUrl}/notifications`;

  unreadCount = signal<number>(0);

  constructor(private http: HttpClient) {}

  getNotifications(page: number = 0, size: number = 20): Observable<ApiResponse<PageResponse<Notification>>> {
    return this.http.get<ApiResponse<PageResponse<Notification>>>(`${this.baseUrl}?page=${page}&size=${size}`);
  }

  fetchUnreadCount(): void {
    this.http.get<ApiResponse<{ unreadCount: number }>>(`${this.baseUrl}/unread-count`).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.unreadCount.set(res.data.unreadCount);
        }
      },
      error: () => {}
    });
  }

  markAsRead(id: number): Observable<ApiResponse<void>> {
    return this.http.patch<ApiResponse<void>>(`${this.baseUrl}/${id}/read`, {}).pipe(
      tap(() => this.fetchUnreadCount())
    );
  }

  markAllAsRead(): Observable<ApiResponse<void>> {
    return this.http.patch<ApiResponse<void>>(`${this.baseUrl}/read-all`, {}).pipe(
      tap(() => this.unreadCount.set(0))
    );
  }
}

