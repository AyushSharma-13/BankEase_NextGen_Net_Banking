import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="error-container">
      <div class="error-card">
        <div class="error-icon">
          <i class="fas fa-compass"></i>
        </div>
        <h1 class="error-code">404</h1>
        <h2 class="error-title">Page Not Found</h2>
        <p class="error-message">
          The banking resource or URL you requested does not exist or has been relocated.
        </p>
        <div class="error-actions">
          <a routerLink="/dashboard" class="btn btn-primary">
            <i class="fas fa-home"></i> Back to Dashboard
          </a>
          <a routerLink="/" class="btn btn-outline">
            <i class="fas fa-globe"></i> Bank Home
          </a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .error-container {
      min-height: 80vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 24px;
    }
    .error-card {
      background: var(--bg-surface);
      border: 1px solid var(--border-color);
      border-radius: var(--radius-xl);
      padding: 48px;
      text-align: center;
      max-width: 500px;
      box-shadow: var(--shadow-lg);
    }
    .error-icon {
      font-size: 3.5rem;
      color: var(--primary-light);
      margin-bottom: 16px;
    }
    .error-code {
      font-size: 4rem;
      font-weight: 800;
      color: var(--primary-color);
      line-height: 1;
      margin-bottom: 8px;
    }
    .error-title {
      font-size: 1.5rem;
      font-weight: 700;
      margin-bottom: 12px;
    }
    .error-message {
      font-size: 0.95rem;
      color: var(--text-secondary);
      margin-bottom: 24px;
      line-height: 1.5;
    }
    .error-actions {
      display: flex;
      justify-content: center;
      gap: 12px;
    }
  `]
})
export class NotFoundComponent {}

