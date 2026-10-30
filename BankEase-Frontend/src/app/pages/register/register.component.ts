import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  name = '';
  email = '';
  phone = '';
  address = '';
  username = '';
  password = '';
  accountType = 'SAVINGS';
  initialDeposit = 5000;

  loading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.name || !this.email || !this.phone || !this.address || !this.username || !this.password) {
      this.errorMessage.set('Please fill out all required fields.');
      return;
    }

    if (this.password.length < 6) {
      this.errorMessage.set('Password must be at least 6 characters long.');
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const payload = {
      name: this.name,
      email: this.email,
      phone: this.phone,
      address: this.address,
      username: this.username,
      password: this.password,
      accountType: this.accountType,
      initialDeposit: this.initialDeposit
    };

    this.authService.register(payload).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(
          err.error?.message || 'Registration failed. Please check your information and try again.'
        );
      }
    });
  }
}

