import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InvestmentService } from '../../core/services/investment.service';
import { Investment, InvestmentPlanInfo, InvestmentRequest } from '../../core/models/investment.model';

@Component({
  selector: 'app-investments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './investments.component.html',
  styleUrls: ['./investments.component.css']
})
export class InvestmentsComponent implements OnInit {
  plans: InvestmentPlanInfo[] = [];
  investments: Investment[] = [];
  isLoading = false;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  // Calculator state
  calcAmount = 50000;
  calcTenure = 24;
  calcRate = 7.5;
  calculatedMaturity = 0;
  calculatedInterest = 0;

  // Modal state
  showModal = false;
  newInvestment: InvestmentRequest = {
    investmentType: 'FIXED_DEPOSIT',
    amount: 25000,
    tenureMonths: 12
  };

  constructor(private investmentService: InvestmentService) {}

  ngOnInit(): void {
    this.loadPlans();
    this.loadInvestments();
    this.computeReturns();
  }

  loadPlans(): void {
    this.investmentService.getInvestmentPlans().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.plans = res.data;
        }
      },
      error: () => {
        // Fallback plans if backend returns empty
        this.plans = [
          { type: 'FIXED_DEPOSIT', name: 'Fixed Deposit (FD)', returnRate: 7.25, minAmount: 10000, riskLevel: 'Low Risk', description: 'Guaranteed returns with sovereign-grade capital safety.' },
          { type: 'RECURRING_DEPOSIT', name: 'Recurring Deposit (RD)', returnRate: 6.80, minAmount: 1000, riskLevel: 'Low Risk', description: 'Systematic monthly savings with guaranteed compounded interest.' },
          { type: 'MUTUAL_FUNDS', name: 'Bluechip Equity Mutual Fund', returnRate: 12.50, minAmount: 5000, riskLevel: 'Moderate Risk', description: 'Market-linked diversified equity portfolio for wealth creation.' },
          { type: 'NATIONAL_PENSION_SCHEME', name: 'National Pension Scheme (NPS)', returnRate: 9.80, minAmount: 5000, riskLevel: 'Low to Moderate', description: 'Government-backed retirement plan with triple tax exemptions.' }
        ];
      }
    });
  }

  loadInvestments(): void {
    this.isLoading = true;
    this.investmentService.getMyInvestments(0, 20).subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success && res.data) {
          this.investments = res.data.content;
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Failed to load investments.';
      }
    });
  }

  openInvestModal(plan?: InvestmentPlanInfo): void {
    if (plan) {
      this.newInvestment.investmentType = plan.type;
      this.newInvestment.amount = plan.minAmount;
      this.calcRate = plan.returnRate;
    }
    this.errorMessage = '';
    this.successMessage = '';
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  computeReturns(): void {
    const P = Number(this.calcAmount) || 0;
    const t = (Number(this.calcTenure) || 12) / 12;
    const r = (Number(this.calcRate) || 7.5) / 100;
    // Compound interest annually: A = P(1 + r)^t
    this.calculatedMaturity = Math.round(P * Math.pow(1 + r, t));
    this.calculatedInterest = Math.round(this.calculatedMaturity - P);
  }

  onPlanChange(): void {
    const found = this.plans.find(p => p.type === this.newInvestment.investmentType);
    if (found) {
      this.calcRate = found.returnRate;
    }
  }

  submitInvestment(): void {
    if (!this.newInvestment.amount || this.newInvestment.amount <= 0) {
      this.errorMessage = 'Please enter a valid investment amount.';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.investmentService.applyInvestment(this.newInvestment).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        if (res.success) {
          this.successMessage = 'Investment request submitted successfully!';
          this.closeModal();
          this.loadInvestments();
        } else {
          this.errorMessage = res.message;
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.message || 'Investment submission failed.';
      }
    });
  }

  formatType(type: string): string {
    return type ? type.replace(/_/g, ' ') : '';
  }
}
