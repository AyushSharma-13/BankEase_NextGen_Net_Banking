import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransactionService } from '../../core/services/transaction.service';
import { Transaction } from '../../core/models/transaction.model';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.css'
})
export class TransactionsComponent implements OnInit {
  transactions = signal<Transaction[]>([]);
  totalElements = signal<number>(0);
  totalPages = signal<number>(0);
  currentPage = signal<number>(0);
  loading = signal<boolean>(true);

  // Filters
  searchTerm: string = '';
  selectedType: string = '';
  selectedStatus: string = '';

  // Modal Detail
  selectedTxn = signal<Transaction | null>(null);

  constructor(private transactionService: TransactionService) {}

  ngOnInit(): void {
    this.loadTransactions();
  }

  loadTransactions(page: number = 0): void {
    this.loading.set(true);
    this.currentPage.set(page);

    this.transactionService.getTransactions({
      type: this.selectedType || undefined,
      status: this.selectedStatus || undefined,
      search: this.searchTerm || undefined,
      page: page,
      size: 15
    }).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success && res.data) {
          this.transactions.set(res.data.content);
          this.totalElements.set(res.data.totalElements);
          this.totalPages.set(res.data.totalPages);
        }
      },
      error: () => this.loading.set(false)
    });
  }

  applyFilter(): void {
    this.loadTransactions(0);
  }

  resetFilter(): void {
    this.searchTerm = '';
    this.selectedType = '';
    this.selectedStatus = '';
    this.loadTransactions(0);
  }

  viewDetails(txn: Transaction): void {
    this.selectedTxn.set(txn);
  }
}
