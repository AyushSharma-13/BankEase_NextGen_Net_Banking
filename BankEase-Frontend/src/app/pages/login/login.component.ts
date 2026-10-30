import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  username = '';
  password = '';
  loading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  quickFill(user: string, pass: string): void {
    this.username = user;
    this.password = pass;
    this.errorMessage.set(null);
  }

  onSubmit(): void {
    if (!this.username || !this.password) {
      this.errorMessage.set('Please enter both username and password.');
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    this.authService.login({ username: this.username, password: this.password }).subscribe({
      next: (res) => {
        this.loading.set(false);
        const role = res.data.role;
        const returnUrl = this.route.snapshot.queryParams['returnUrl'];

        if (returnUrl) {
          this.router.navigateByUrl(returnUrl);
        } else if (role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else if (role === 'BANK_STAFF') {
          this.router.navigate(['/staff']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(
          err.error?.message || 'Invalid credentials. Please verify your username and password.'
        );
      }
    });
  }
}
