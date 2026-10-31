import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { NotificationService } from '../core/services/notification.service';
import { ConnectionService } from '../core/services/connection.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent implements OnInit {
  sidebarOpen = signal<boolean>(true);
  notificationsDropdownOpen = signal<boolean>(false);
  profileDropdownOpen = signal<boolean>(false);
  backendModalOpen = signal<boolean>(false);
  customBackendInput = '';

  constructor(
    public authService: AuthService,
    public notificationService: NotificationService,
    public connectionService: ConnectionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.notificationService.fetchUnreadCount();
    }
    this.customBackendInput = this.connectionService.backendUrl();
  }

  toggleSidebar(): void {
    this.sidebarOpen.update(v => !v);
  }

  toggleNotifications(): void {
    this.notificationsDropdownOpen.update(v => !v);
    this.profileDropdownOpen.set(false);
  }

  toggleProfile(): void {
    this.profileDropdownOpen.update(v => !v);
    this.notificationsDropdownOpen.set(false);
  }

  toggleProfileDropdown(): void {
    this.toggleProfile();
  }

  openBackendModal(): void {
    this.customBackendInput = this.connectionService.backendUrl();
    this.backendModalOpen.set(true);
  }

  closeBackendModal(): void {
    this.backendModalOpen.set(false);
  }

  saveBackendUrl(): void {
    if (this.customBackendInput) {
      this.connectionService.setBackendUrl(this.customBackendInput);
    }
    this.closeBackendModal();
  }

  testBackend(): void {
    this.connectionService.pingBackend();
  }

  resetBackend(): void {
    this.connectionService.resetToDefault();
    this.customBackendInput = this.connectionService.backendUrl();
  }

  logout(): void {
    this.authService.logout();
  }
}
