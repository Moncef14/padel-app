import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Auth } from '../../../services/auth';
import { SiteService } from '../../../services/site';
import { RegisterRequest } from '../../../models/auth.model';
import { TypeMembre } from '../../../models/enums.model';
import { Site } from '../../../models/site.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class Register {

  formulaire: RegisterRequest = {
    nom: '',
    prenom: '',
    email: '',
    motDePasse: '',
    type: TypeMembre.LIBRE,
    siteId: null
  };

  sites = signal<Site[]>([]);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  // Le TypeMembre est exposé au template pour construire le <mat-select>
  readonly typeMembre = TypeMembre;

  // N'affiche le sélecteur de site que pour un membre de type SITE —
  // reflète la règle métier backend (RegisterRequest.siteId obligatoire
  // uniquement si type = SITE, voir MembreService.inscrire()).
  afficherSite(): boolean {
    return this.formulaire.type === TypeMembre.SITE;
  }

  constructor(
    private auth: Auth,
    private siteService: SiteService,
    private router: Router
  ) {
    this.siteService.getActifs().subscribe({
      next: (sites) => this.sites.set(sites)
    });
  }

  onSubmit(): void {
    this.errorMessage.set(null);

    // Si le type n'est pas SITE, on s'assure que siteId reste null
    // même si un site avait été sélectionné avant de changer de type.
    if (this.formulaire.type !== TypeMembre.SITE) {
      this.formulaire.siteId = null;
    }

    this.loading.set(true);
    this.auth.register(this.formulaire).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/membre/mes-matchs']);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(
          err.status === 400
            ? 'Un compte existe déjà avec cet email.'
            : 'Une erreur est survenue lors de l\'inscription.'
        );
      }
    });
  }
}