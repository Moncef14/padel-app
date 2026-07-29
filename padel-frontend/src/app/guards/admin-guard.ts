import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth';

// Protège les routes /admin/* — accessible aux deux rôles ADMIN_GLOBAL
// et ADMIN_SITE. La restriction plus fine (global uniquement) est gérée
// par adminGlobalGuard sur les routes sensibles (ex: gestion des sites).
export const adminGuard: CanActivateFn = (route, state) => {
  const auth = inject(Auth);
  const router = inject(Router);

  if (auth.isLoggedIn() && auth.isAdmin()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};