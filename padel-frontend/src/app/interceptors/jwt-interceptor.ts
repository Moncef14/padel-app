import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Auth } from '../services/auth';

// Ajoute automatiquement le header Authorization: Bearer <token> à chaque
// requête HTTP sortante — évite de devoir le faire manuellement dans
// chaque service. C'est ce que le backend attend dans JwtFilter.java.
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(Auth);
  const token = auth.getToken();

  // Les routes d'authentification (login/register) n'ont pas besoin de token
  const estRoutePublique = req.url.includes('/api/auth/');

  if (token && !estRoutePublique) {
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedReq);
  }

  return next(req);
};