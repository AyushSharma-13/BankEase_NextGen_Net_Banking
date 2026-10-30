import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoanService } from '../../core/services/loan.service';
import { InvestmentService } from '../../core/services/investment.service';
import { Loan } from '../../core/models/loan.model';
import { Investment } from '../../core/models/investment.model';

@Component({
  selector: 'app-staff',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './staff.component.html',
  styleUrls: ['./staff.component.css']
})
export class StaffComponent implements OnInit {
  loans: Loan[] = [];
  investments: Investment[] = [];
  activeTab: 'LOANS' | 'INVESTMENTS' = 'LOANS';

  isLoading = false;
  isProcessing = false;
  errorMessage = '';
  successMessage = '';

  // Decision Modal
  showDecisionModal = false;
  selectedType: 'LOAN' | 'INVESTMENT' = 'LOAN';
  selectedItemId: number = 0;
  decisionStatus: 'APPROVED' | 'REJECTED' | 'ACTIVE' | 'CANCELLED' = 'APPROVED';
  decisionRemarks = '';

  constructor(
    private loanService: LoanService,
    private investmentService: InvestmentService
  ) {}

  ngOnInit(): void {
    this.loadLoans();
    this.loadInvestments();
  }

  loadLoans(): void {
    this.isLoading = true;
    this.loanService.getAllStaffLoans(0, 50).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success && res.data) {
          this.loans = res.data.content;
        }
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  loadInvestments(): void {
    this.investmentService.getAllStaffInvestments(0, 50).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.investments = res.data.content;
        }
      }
    });
  }

  openLoanDecision(loanId: number, status: 'APPROVED' | 'REJECTED'): void {
    this.selectedType = 'LOAN';
    this.selectedItemId = loanId;
    this.decisionStatus = status;
    this.decisionRemarks = status === 'APPROVED' ? 'Application meets KYC & credit risk requirements.' : 'Debt-to-income threshold exceeded.';
    this.showDecisionModal = true;
  }

  openInvestmentDecision(invId: number, status: 'ACTIVE' | 'CANCELLED'): void {
    this.selectedType = 'INVESTMENT';
    this.selectedItemId = invId;
    this.decisionStatus = status;
    this.decisionRemarks = status === 'ACTIVE' ? 'Funds earmarked and portfolio created.' : 'Customer requested cancellation.';
    this.showDecisionModal = true;
  }

  closeModal(): void {
    this.showDecisionModal = false;
  }

  confirmDecision(): void {
    this.isProcessing = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.selectedType === 'LOAN') {
      this.loanService.updateLoanStatus(this.selectedItemId, this.decisionStatus, this.decisionRemarks).subscribe({
        next: (res) => {
          this.isProcessing = false;
          if (res.success) {
            this.successMessage = `Loan #${this.selectedItemId} has been marked as ${this.decisionStatus}.`;
            this.closeModal();
            this.loadLoans();
          } else {
            this.errorMessage = res.message;
          }
        },
        error: (err) => {
          this.isProcessing = false;
          this.errorMessage = err.error?.message || 'Failed to update loan status.';
        }
      });
    } else {
      this.investmentService.updateInvestmentStatus(this.selectedItemId, this.decisionStatus, this.decisionRemarks).subscribe({
        next: (res) => {
          this.isProcessing = false;
          if (res.success) {
            this.successMessage = `Investment #${this.selectedItemId} has been marked as ${this.decisionStatus}.`;
            this.closeModal();
            this.loadInvestments();
          } else {
            this.errorMessage = res.message;
          }
        },
        error: (err) => {
          this.isProcessing = false;
          this.errorMessage = err.error?.message || 'Failed to update investment status.';
        }
      });
    }
  }

  formatType(type: string): string {
    return type ? type.replace(/_/g, ' ') : '';
  }
}

