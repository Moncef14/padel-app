import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Auth } from '../../../services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, MatButtonModule, MatIconModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class Navbar {

  // auth public (pas privé) : le template lit directement isMembre()/isAdmin()/isAdminGlobal()
  // pour choisir quels liens afficher — cf. navbar.html, la hiérarchie de menus suit celle des guards
  // (isAdminGlobal implique isAdmin, donc ses liens s'ajoutent à ceux d'un admin plutôt que de les remplacer)
  constructor(public auth: Auth, private router: Router) {}

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}