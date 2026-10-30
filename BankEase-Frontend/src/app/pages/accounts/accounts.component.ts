import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AccountService } from '../../core/services/account.service';
import { Account } from '../../core/models/account.model';
import { Transaction } from '../../core/models/transaction.model';

@Component({
  selector: 'app-accounts',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './accounts.component.html',
  styleUrl: './accounts.component.css'
})
export class AccountsComponent implements OnInit {
  accounts = signal<Account[]>([]);
  selectedAccount = signal<Account | null>(null);
  transactions = signal<Transaction[]>([]);
  loading = signal<boolean>(true);
  downloading = signal<boolean>(false);
  copiedAccount = signal<boolean>(false);

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.loading.set(true);
    this.accountService.getAccounts().subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success && res.data) {
          this.accounts.set(res.data);
          if (res.data.length > 0) {
            this.selectAccount(res.data[0]);
          }
        }
      },
      error: () => this.loading.set(false)
    });
  }

  selectAccount(acc: Account): void {
    this.selectedAccount.set(acc);
    this.loadAccountTransactions(acc.accountId);
  }

  loadAccountTransactions(accountId: number): void {
    this.accountService.getAccountTransactions(accountId, 0, 20).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.transactions.set(res.data.content);
        }
      }
    });
  }

  downloadStatement(accountId: number): void {
    this.downloading.set(true);
    this.accountService.downloadStatement(accountId).subscribe({
      next: (blob) => {
        this.downloading.set(false);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `BankEase_Statement_${this.selectedAccount()?.accountNo}.txt`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => this.downloading.set(false)
    });
  }

  copyAccountNo(accNo: string): void {
    navigator.clipboard.writeText(accNo);
    this.copiedAccount.set(true);
    setTimeout(() => this.copiedAccount.set(false), 2000);
  }
}

