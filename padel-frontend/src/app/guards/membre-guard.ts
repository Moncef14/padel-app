import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth';

// Protège les routes /membre/* — accessible uniquement à un membre connecté
// (pas un admin). On distingue membre/admin via la présence du matricule
// dans l'état d'authentification (voir AuthState).
export const membreGuard: CanActivateFn = (route, state) => {
  const auth = inject(Auth);
  const router = inject(Router);

  if (auth.isLoggedIn() && auth.isMembre()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};