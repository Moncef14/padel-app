import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Auth } from '../../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {

  identifiant = '';
  motDePasse = '';
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(private auth: Auth, private router: Router) {}

  // Un identifiant contenant '@' est traité comme un email admin,
  // sinon comme un matricule membre (format G0001 / S0003 / L0008).
  // Cette distinction reflète l'énoncé SGBD : "pas de login nécessaire
  // pour les users, uniquement le matricule" — l'email reste réservé
  // aux comptes administrateurs.
  private estEmail(valeur: string): boolean {
    return valeur.includes('@');
  }

  onSubmit(): void {
    this.errorMessage.set(null);
    this.loading.set(true);

    if (this.estEmail(this.identifiant)) {
      this.auth.loginAdmin({ email: this.identifiant, password: this.motDePasse }).subscribe({
        next: () => {
          this.loading.set(false);
          this.router.navigate(['/admin/dashboard']);
        },
        error: () => {
          this.loading.set(false);
          this.errorMessage.set('Email ou mot de passe incorrect.');
        }
      });
    } else {
      this.auth.loginMembre({ matricule: this.identifiant, motDePasse: this.motDePasse }).subscribe({
        next: () => {
          this.loading.set(false);
          this.router.navigate(['/membre/mes-matchs']);
        },
        error: () => {
          this.loading.set(false);
          this.errorMessage.set('Matricule ou mot de passe incorrect.');
        }
      });
    }
  }
}