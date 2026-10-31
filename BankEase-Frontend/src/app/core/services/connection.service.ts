import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ConnectionService {
  private readonly STORAGE_KEY = 'bankease_backend_url';
  
  backendUrl = signal<string>(this.getInitialBackendUrl());
  isOnline = signal<boolean>(false);
  isChecking = signal<boolean>(false);
  lastChecked = signal<Date | null>(null);

  constructor(private http: HttpClient) {
    this.checkUrlParams();
    this.pingBackend();
  }

  private getInitialBackendUrl(): string {
    if (typeof window !== 'undefined') {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      if (stored) return stored;
    }
    return environment.apiUrl;
  }

  private checkUrlParams(): void {
    if (typeof window !== 'undefined' && window.location) {
      const params = new URLSearchParams(window.location.search);
      const backendParam = params.get('backend');
      if (backendParam) {
        this.setBackendUrl(backendParam);
      }
    }
  }

  setBackendUrl(url: string): void {
    let cleanUrl = url.trim().replace(/\/+$/, '');
    if (!cleanUrl.endsWith('/api') && !cleanUrl.includes('/api/')) {
      cleanUrl += '/api';
    }
    if (typeof window !== 'undefined') {
      localStorage.setItem(this.STORAGE_KEY, cleanUrl);
    }
    this.backendUrl.set(cleanUrl);
    this.pingBackend();
  }

  pingBackend(): void {
    this.isChecking.set(true);
    const target = this.backendUrl();
    let healthUrl: string;

    if (target.startsWith('http://') || target.startsWith('https://')) {
      healthUrl = target.replace(/\/api\/?$/, '') + '/actuator/health';
    } else {
      healthUrl = '/actuator/health';
    }

    this.http.get<{ status: string }>(healthUrl).subscribe({
      next: (res) => {
        this.isChecking.set(false);
        this.isOnline.set(res?.status === 'UP');
        this.lastChecked.set(new Date());
      },
      error: () => {
        this.isChecking.set(false);
        this.isOnline.set(false);
        this.lastChecked.set(new Date());
      }
    });
  }

  resetToDefault(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem(this.STORAGE_KEY);
    }
    this.backendUrl.set(environment.apiUrl);
    this.pingBackend();
  }
}
