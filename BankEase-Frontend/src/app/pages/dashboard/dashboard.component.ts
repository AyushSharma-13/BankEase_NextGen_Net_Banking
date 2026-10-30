import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AccountService } from '../../core/services/account.service';
import { DashboardOverview } from '../../core/models/account.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  overview = signal<DashboardOverview | null>(null);
  loading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);
  copiedAccount = signal<boolean>(false);

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading.set(true);
    this.accountService.getDashboard().subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success && res.data) {
          this.overview.set(res.data);
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load banking dashboard data.');
      }
    });
  }

  copyAccountNo(accNo: string): void {
    navigator.clipboard.writeText(accNo);
    this.copiedAccount.set(true);
    setTimeout(() => this.copiedAccount.set(false), 2000);
  }
}

