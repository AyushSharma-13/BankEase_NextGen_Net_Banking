import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles = route.data?.['roles'] as Array<string>;
  const userRole = authService.getRole();

  if (authService.isAuthenticated() && expectedRoles && expectedRoles.includes(userRole || '')) {
    return true;
  }

  // If unauthorized, redirect to standard dashboard or login
  if (userRole === 'ADMIN') {
    router.navigate(['/admin']);
  } else if (userRole === 'BANK_STAFF') {
    router.navigate(['/staff']);
  } else {
    router.navigate(['/dashboard']);
  }

  return false;
};
