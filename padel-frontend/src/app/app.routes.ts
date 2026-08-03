import { Routes } from '@angular/router';

import { authGuard } from './guards/auth-guard';
import { membreGuard } from './guards/membre-guard';
import { adminGuard } from './guards/admin-guard';
import { adminGlobalGuard } from './guards/admin-global-guard';

import { Login } from './components/auth/login/login';
import { Register } from './components/auth/register/register';

import { MesMatchs } from './components/membre/mes-matchs/mes-matchs';
import { MatchDetail } from './components/membre/match-detail/match-detail';
import { Reserver } from './components/membre/reserver/reserver';
import { MatchsPublics } from './components/membre/matchs-publics/matchs-publics';

import { Dashboard } from './components/admin/dashboard/dashboard';
import { Sites } from './components/admin/sites/sites';
import { Terrains } from './components/admin/terrains/terrains';
import { Membres } from './components/admin/membres/membres';
import { Matchs } from './components/admin/matchs/matchs';
import { Fermetures } from './components/admin/fermetures/fermetures';
import { Admins } from './components/admin/admins/admins';

// Structure de la SPA en miroir des controllers backend :
// - routes publiques pour l'authentification
// - /membre/* protégé par membreGuard (rôle membre uniquement)
// - /admin/* protégé par adminGuard, avec certaines sous-routes
//   restreintes à adminGlobalGuard (create/delete sites, gestion admins)
//   pour respecter @PreAuthorize("hasRole('ADMIN_GLOBAL')") côté backend
export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Authentification (publiques)
  { path: 'login', component: Login },
  { path: 'register', component: Register },

  // Espace membre
  {
    path: 'membre',
    canActivate: [membreGuard],
    children: [
      { path: '', redirectTo: 'mes-matchs', pathMatch: 'full' },
      { path: 'mes-matchs', component: MesMatchs },
      { path: 'match/:id', component: MatchDetail },
      { path: 'reserver', component: Reserver },
      { path: 'publics', component: MatchsPublics }
    ]
  },

  // Espace admin
  {
    path: 'admin',
    canActivate: [adminGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: Dashboard },
      { path: 'sites', component: Sites, canActivate: [adminGlobalGuard] },
      { path: 'terrains', component: Terrains },
      { path: 'membres', component: Membres },
      { path: 'matchs', component: Matchs },
      { path: 'fermetures', component: Fermetures },
      { path: 'admins', component: Admins, canActivate: [adminGlobalGuard] }
    ]
  },

  // Catch-all → redirige vers login si route inconnue
  { path: '**', redirectTo: '/login' }
];