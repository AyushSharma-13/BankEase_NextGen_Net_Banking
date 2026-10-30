import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../core/services/admin.service';
import { SystemMetrics, AuditLog } from '../../core/models/admin.model';
import { UserProfile } from '../../core/models/auth.model';
import { Account } from '../../core/models/account.model';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  metrics: SystemMetrics | null = null;
  users: UserProfile[] = [];
  accounts: Account[] = [];
  auditLogs: AuditLog[] = [];

  activeTab: 'OVERVIEW' | 'USERS' | 'ACCOUNTS' | 'AUDIT' = 'OVERVIEW';
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // New staff modal
  showStaffModal = false;
  newStaff = {
    username: '',
    password: '',
    name: '',
    email: '',
    phone: ''
  };
  isCreatingStaff = false;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadMetrics();
    this.loadUsers();
    this.loadAccounts();
    this.loadAuditLogs();
  }

  loadMetrics(): void {
    this.adminService.getMetrics().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.metrics = res.data;
        }
      },
      error: () => {}
    });
  }

  loadUsers(): void {
    this.adminService.getUsers(0, 50).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.users = res.data.content;
        }
      }
    });
  }

  loadAccounts(): void {
    this.adminService.getAccounts(0, 50).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.accounts = res.data.content;
        }
      }
    });
  }

  loadAuditLogs(): void {
    this.adminService.getAuditLogs(0, 50).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.auditLogs = res.data.content;
        }
      }
    });
  }

  toggleUserStatus(user: UserProfile): void {
    this.adminService.toggleUser(user.userId).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          user.enabled = res.data.enabled;
          this.successMessage = `User status updated to ${user.enabled ? 'Active' : 'Inactive'}.`;
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update user status.';
      }
    });
  }

  changeAccountStatus(account: Account, newStatus: string): void {
    this.adminService.updateAccountStatus(account.accountId, newStatus).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          account.status = res.data.status;
          this.successMessage = `Account ${account.accountNo || account.accountNumber} status changed to ${newStatus}.`;
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update account status.';
      }
    });
  }

  openStaffModal(): void {
    this.newStaff = { username: '', password: '', name: '', email: '', phone: '' };
    this.errorMessage = '';
    this.successMessage = '';
    this.showStaffModal = true;
  }

  closeStaffModal(): void {
    this.showStaffModal = false;
  }

  createStaffMember(): void {
    if (!this.newStaff.username || !this.newStaff.password || !this.newStaff.name) {
      this.errorMessage = 'Please provide username, password, and full name.';
      return;
    }

    this.isCreatingStaff = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.adminService.createStaff(this.newStaff).subscribe({
      next: (res) => {
        this.isCreatingStaff = false;
        if (res.success) {
          this.successMessage = `Bank staff member ${this.newStaff.username} provisioned successfully!`;
          this.closeStaffModal();
          this.loadUsers();
          this.loadMetrics();
        } else {
          this.errorMessage = res.message;
        }
      },
      error: (err) => {
        this.isCreatingStaff = false;
        this.errorMessage = err.error?.message || 'Failed to create staff member.';
      }
    });
  }
}
