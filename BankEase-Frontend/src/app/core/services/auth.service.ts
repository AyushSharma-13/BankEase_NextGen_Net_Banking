import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { AuthResponse, UserProfile } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'bankease_token';
  private readonly USER_KEY = 'bankease_user';

  // Reactive state using Angular Signals
  currentUser = signal<AuthResponse | null>(this.getStoredUser());

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: { username: string; password: string }): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/login`, credentials).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.setSession(res.data);
        }
      })
    );
  }

  register(customerData: any): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/register`, customerData).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.setSession(res.data);
        }
      })
    );
  }

  getProfile(): Observable<ApiResponse<UserProfile>> {
    return this.http.get<ApiResponse<UserProfile>>(`${this.baseUrl}/me`);
  }

  changePassword(data: { currentPassword: string; newPassword: string }): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.baseUrl}/change-password`, data);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getRole(): string | null {
    return this.currentUser()?.role || null;
  }

  private setSession(auth: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, auth.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(auth));
    this.currentUser.set(auth);
  }

  private getStoredUser(): AuthResponse | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    if (!userStr) return null;
    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  }
}
