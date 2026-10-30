import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserProfile } from '../../core/models/auth.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile: UserProfile | null = null;
  isLoading = false;
  isChangingPassword = false;
  successMessage = '';
  errorMessage = '';

  passwordData = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.isLoading = true;
    this.authService.getProfile().subscribe({
      next: (res) => {
        this.isLoading = false;
        if (res.success && res.data) {
          this.profile = res.data;
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Failed to load user profile.';
      }
    });
  }

  submitPasswordChange(): void {
    if (!this.passwordData.currentPassword || !this.passwordData.newPassword) {
      this.errorMessage = 'Please fill out all password fields.';
      return;
    }

    if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
      this.errorMessage = 'New passwords do not match.';
      return;
    }

    if (this.passwordData.newPassword.length < 8) {
      this.errorMessage = 'New password must be at least 8 characters.';
      return;
    }

    this.isChangingPassword = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.changePassword({
      currentPassword: this.passwordData.currentPassword,
      newPassword: this.passwordData.newPassword
    }).subscribe({
      next: (res) => {
        this.isChangingPassword = false;
        if (res.success) {
          this.successMessage = 'Your password has been changed successfully!';
          this.passwordData = { currentPassword: '', newPassword: '', confirmPassword: '' };
        } else {
          this.errorMessage = res.message;
        }
      },
      error: (err) => {
        this.isChangingPassword = false;
        this.errorMessage = err.error?.message || 'Password update failed. Check your current password.';
      }
    });
  }
}

