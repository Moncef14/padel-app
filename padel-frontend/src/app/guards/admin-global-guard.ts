import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth';

// Restreint certaines routes à l'ADMIN_GLOBAL uniquement, en miroir de
// @PreAuthorize("hasRole('ADMIN_GLOBAL')") côté backend (SiteController,
// AdministrateurController). Un ADMIN_SITE authentifié mais pas global
// est redirigé vers son dashboard plutôt que vers le login.
export const adminGlobalGuard: CanActivateFn = (route, state) => {
  const auth = inject(Auth);
  const router = inject(Router);

  if (auth.isLoggedIn() && auth.isAdminGlobal()) {
    return true;
  }

  if (auth.isLoggedIn() && auth.isAdmin()) {
    router.navigate(['/admin/dashboard']);
  } else {
    router.navigate(['/login']);
  }
  return false;
};