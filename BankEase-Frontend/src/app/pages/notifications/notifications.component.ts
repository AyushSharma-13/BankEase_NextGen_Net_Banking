import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../core/services/notification.service';
import { Notification } from '../../core/models/notification.model';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {
  notifications: Notification[] = [];
  filteredNotifications: Notification[] = [];
  isLoading = false;
  activeFilter: 'ALL' | 'UNREAD' = 'ALL';
  errorMessage = '';

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.isLoading = true;
    this.notificationService.getNotifications(0, 50).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success && res.data) {
          this.notifications = res.data.content;
          this.applyFilter();
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Failed to load notifications.';
      }
    });
  }

  setFilter(filter: 'ALL' | 'UNREAD'): void {
    this.activeFilter = filter;
    this.applyFilter();
  }

  applyFilter(): void {
    if (this.activeFilter === 'UNREAD') {
      this.filteredNotifications = this.notifications.filter(n => !n.isRead);
    } else {
      this.filteredNotifications = [...this.notifications];
    }
  }

  markAsRead(notification: Notification): void {
    if (notification.isRead) return;
    this.notificationService.markAsRead(notification.notificationId).subscribe({
      next: () => {
        notification.isRead = true;
        this.applyFilter();
      }
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.notifications.forEach(n => n.isRead = true);
        this.applyFilter();
      }
    });
  }

  getIcon(type: string): string {
    switch (type) {
      case 'TRANSFER':
      case 'TRANSACTION':
        return 'fa-exchange-alt';
      case 'BILL_PAYMENT':
        return 'fa-file-invoice-dollar';
      case 'LOAN':
        return 'fa-hand-holding-usd';
      case 'INVESTMENT':
        return 'fa-chart-line';
      case 'SECURITY':
        return 'fa-shield-alt';
      default:
        return 'fa-bell';
    }
  }
}

