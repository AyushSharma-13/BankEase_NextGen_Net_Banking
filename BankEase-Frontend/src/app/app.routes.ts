import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { LayoutComponent } from './layout/layout.component';

// Pages
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { AccountsComponent } from './pages/accounts/accounts.component';
import { TransfersComponent } from './pages/transfers/transfers.component';
import { BeneficiariesComponent } from './pages/beneficiaries/beneficiaries.component';
import { TransactionsComponent } from './pages/transactions/transactions.component';
import { BillsComponent } from './pages/bills/bills.component';
import { LoansComponent } from './pages/loans/loans.component';
import { InvestmentsComponent } from './pages/investments/investments.component';
import { NotificationsComponent } from './pages/notifications/notifications.component';
import { AdminComponent } from './pages/admin/admin.component';
import { StaffComponent } from './pages/staff/staff.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';

export const routes: Routes = [
  // Public Routes
  { path: '', component: LandingComponent, pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // Authenticated Portal Routes (Wrapped inside LayoutComponent)
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'accounts', component: AccountsComponent },
      { path: 'transfers', component: TransfersComponent },
      { path: 'beneficiaries', component: BeneficiariesComponent },
      { path: 'transactions', component: TransactionsComponent },
      { path: 'bills', component: BillsComponent },
      { path: 'loans', component: LoansComponent },
      { path: 'investments', component: InvestmentsComponent },
      { path: 'notifications', component: NotificationsComponent },
      { path: 'profile', component: ProfileComponent },
      {
        path: 'staff',
        component: StaffComponent,
        canActivate: [roleGuard],
        data: { roles: ['BANK_STAFF', 'ADMIN'] }
      },
      {
        path: 'admin',
        component: AdminComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      }
    ]
  },

  // 404 Wildcard
  { path: '**', component: NotFoundComponent }
];
