import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { NotificationService } from '../core/services/notification.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent implements OnInit {
  sidebarOpen = signal<boolean>(true);
  notificationsDropdownOpen = signal<boolean>(false);
  profileDropdownOpen = signal<boolean>(false);

  constructor(
    public authService: AuthService,
    public notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.notificationService.fetchUnreadCount();
    }
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

  logout(): void {
    this.authService.logout();
  }
}

