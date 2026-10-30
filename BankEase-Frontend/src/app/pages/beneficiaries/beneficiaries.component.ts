import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { BeneficiaryService } from '../../core/services/beneficiary.service';
import { Beneficiary } from '../../core/models/beneficiary.model';

@Component({
  selector: 'app-beneficiaries',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './beneficiaries.component.html',
  styleUrl: './beneficiaries.component.css'
})
export class BeneficiariesComponent implements OnInit {
  beneficiaries = signal<Beneficiary[]>([]);
  loading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  // Modal State
  showModal = signal<boolean>(false);
  isEditing = signal<boolean>(false);
  editingId: number | null = null;

  name = '';
  accountNo = '';
  bankName = 'BankEase Net Bank';
  ifscCode = 'BEAS0001001';

  constructor(private beneficiaryService: BeneficiaryService) {}

  ngOnInit(): void {
    this.loadBeneficiaries();
  }

  loadBeneficiaries(): void {
    this.loading.set(true);
    this.beneficiaryService.getBeneficiaries().subscribe({
      next: (res) => {
        this.loading.set(false);
        if (res.success && res.data) {
          this.beneficiaries.set(res.data);
        }
      },
      error: () => this.loading.set(false)
    });
  }

  openAddModal(): void {
    this.isEditing.set(false);
    this.editingId = null;
    this.name = '';
    this.accountNo = '';
    this.bankName = 'BankEase Net Bank';
    this.ifscCode = 'BEAS0001001';
    this.errorMessage.set(null);
    this.showModal.set(true);
  }

  openEditModal(b: Beneficiary): void {
    this.isEditing.set(true);
    this.editingId = b.beneficiaryId;
    this.name = b.name;
    this.accountNo = b.accountNo;
    this.bankName = b.bankName;
    this.ifscCode = b.ifscCode;
    this.errorMessage.set(null);
    this.showModal.set(true);
  }

  saveBeneficiary(): void {
    if (!this.name || !this.accountNo || !this.bankName || !this.ifscCode) {
      this.errorMessage.set('All fields are required.');
      return;
    }

    const payload = {
      name: this.name,
      accountNo: this.accountNo,
      bankName: this.bankName,
      ifscCode: this.ifscCode.toUpperCase()
    };

    if (this.isEditing() && this.editingId) {
      this.beneficiaryService.updateBeneficiary(this.editingId, payload).subscribe({
        next: () => {
          this.showModal.set(false);
          this.loadBeneficiaries();
        },
        error: (err) => this.errorMessage.set(err.error?.message || 'Update failed.')
      });
    } else {
      this.beneficiaryService.addBeneficiary(payload).subscribe({
        next: () => {
          this.showModal.set(false);
          this.loadBeneficiaries();
        },
        error: (err) => this.errorMessage.set(err.error?.message || 'Failed to add beneficiary.')
      });
    }
  }

  deleteBeneficiary(id: number): void {
    if (confirm('Are you sure you want to remove this beneficiary?')) {
      this.beneficiaryService.deleteBeneficiary(id).subscribe({
        next: () => this.loadBeneficiaries()
      });
    }
  }
}
