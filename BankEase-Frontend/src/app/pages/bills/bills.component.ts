import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BillService } from '../../core/services/bill.service';
import { AccountService } from '../../core/services/account.service';
import { Account } from '../../core/models/account.model';
import { BillPayment } from '../../core/models/bill.model';

@Component({
  selector: 'app-bills',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bills.component.html',
  styleUrl: './bills.component.css'
})
export class BillsComponent implements OnInit {
  activeTab = signal<'pay' | 'history'>('pay');

  accounts = signal<Account[]>([]);
  history = signal<BillPayment[]>([]);

  // Bill Form
  selectedCategory = signal<string>('ELECTRICITY');
  billerName: string = 'BESCOM Bangalore';
  consumerNumber: string = '';
  amount: number | null = null;
  accountId: number | null = null;

  loading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  successBill = signal<BillPayment | null>(null);

  categoryBillers: Record<string, string[]> = {
    'ELECTRICITY': ['BESCOM Bangalore', 'Tata Power Mumbai', 'Adani Electricity', 'BSES Rajdhani Delhi'],
    'WATER': ['Bangalore Water Supply (BWSSB)', 'Delhi Jal Board', 'MCGM Water Mumbai'],
    'MOBILE_RECHARGE': ['Jio Prepaid/Postpaid', 'Airtel Telecommunications', 'Vodafone Idea (Vi)'],
    'INTERNET': ['Airtel Broadband Fiber', 'JioFiber Highspeed', 'ACT Fibernet', 'Tata Play Fiber'],
    'GAS': ['Indane Gas LPG', 'Bharat Gas', 'Mahanagar Gas Mumbai', 'Indraprastha Gas Delhi']
  };

  constructor(
    private billService: BillService,
    private accountService: AccountService
  ) {}

  ngOnInit(): void {
    this.accountService.getAccounts().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.accounts.set(res.data);
          if (res.data.length > 0) {
            this.accountId = res.data[0].accountId;
          }
        }
      }
    });
    this.loadHistory();
  }

  loadHistory(): void {
    this.billService.getBillHistory(0, 20).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.history.set(res.data.content);
        }
      }
    });
  }

  selectCategory(cat: string): void {
    this.selectedCategory.set(cat);
    const billers = this.categoryBillers[cat];
    if (billers && billers.length > 0) {
      this.billerName = billers[0];
    }
  }

  payBill(): void {
    if (!this.accountId || !this.billerName || !this.consumerNumber || !this.amount || this.amount <= 0) {
      this.errorMessage.set('Please fill out all bill payment fields correctly.');
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const payload = {
      accountId: this.accountId,
      category: this.selectedCategory(),
      billerName: this.billerName,
      consumerNumber: this.consumerNumber,
      amount: this.amount
    };

    this.billService.payBill(payload).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success && res.data) {
          this.successBill.set(res.data);
          this.loadHistory();
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message || 'Bill payment failed.');
      }
    });
  }

  reset(): void {
    this.successBill.set(null);
    this.consumerNumber = '';
    this.amount = null;
  }
}
