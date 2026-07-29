import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth';

// Le gendarme le plus basique : vérifie juste qu'un token est présent.
// Si non connecté, on redirige vers le login admin par défaut.
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(Auth);
  const router = inject(Router);

  if (auth.isLoggedIn()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};