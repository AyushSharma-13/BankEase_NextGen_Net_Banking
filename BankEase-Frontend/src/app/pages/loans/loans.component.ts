import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoanService } from '../../core/services/loan.service';
import { Loan, LoanTypeInfo } from '../../core/models/loan.model';

@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './loans.component.html',
  styleUrl: './loans.component.css'
})
export class LoansComponent implements OnInit {
  loanTypes = signal<LoanTypeInfo[]>([]);
  myLoans = signal<Loan[]>([]);
  loading = signal<boolean>(true);

  // EMI Calculator State
  calcAmount: number = 1000000;
  calcRate: number = 8.5;
  calcTenureMonths: number = 120;
  calculatedEmi = signal<number>(0);

  // Apply Modal State
  showApplyModal = signal<boolean>(false);
  applyType: string = 'HOME';
  applyAmount: number = 1000000;
  applyTenure: number = 120;
  applyRemarks: string = '';
  modalEmi = signal<number>(0);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  constructor(private loanService: LoanService) {}

  ngOnInit(): void {
    this.calculateEmi();
    this.loadData();
  }

  loadData(): void {
    this.loading.set(true);
    this.loanService.getLoanTypes().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.loanTypes.set(res.data);
        }
      }
    });

    this.loanService.getMyLoans().subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success && res.data) {
          this.myLoans.set(res.data.content);
        }
      },
      error: () => this.loading.set(false)
    });
  }

  calculateEmi(): void {
    const p = this.calcAmount;
    const r = this.calcRate / (12 * 100);
    const n = this.calcTenureMonths;

    if (p > 0 && r > 0 && n > 0) {
      const emi = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
      this.calculatedEmi.set(Math.round(emi));
    }
  }

  openApplyModal(typeInfo?: LoanTypeInfo): void {
    if (typeInfo) {
      this.applyType = typeInfo.type;
      this.applyAmount = typeInfo.minAmount;
      this.applyTenure = Math.min(60, typeInfo.maxTenureMonths);
    }
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.updateModalEmi();
    this.showApplyModal.set(true);
  }

  updateModalEmi(): void {
    const typeInfo = this.loanTypes().find(t => t.type === this.applyType);
    const rate = typeInfo ? typeInfo.interestRate : 10.0;
    const p = this.applyAmount;
    const r = rate / (12 * 100);
    const n = this.applyTenure;

    if (p > 0 && r > 0 && n > 0) {
      const emi = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
      this.modalEmi.set(Math.round(emi));
    }
  }

  submitApplication(): void {
    if (!this.applyAmount || this.applyAmount <= 0 || !this.applyTenure || this.applyTenure <= 0) {
      this.errorMessage.set('Please provide valid loan amount and tenure.');
      return;
    }

    this.loanService.applyLoan({
      loanType: this.applyType,
      amount: this.applyAmount,
      tenureMonths: this.applyTenure,
      remarks: this.applyRemarks
    }).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.showApplyModal.set(false);
          this.loadData();
        }
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Failed to submit loan application.');
      }
    });
  }
}
