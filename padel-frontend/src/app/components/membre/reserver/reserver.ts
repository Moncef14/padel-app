import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatchService } from '../../../services/match';
import { SiteService } from '../../../services/site';
import { TerrainService } from '../../../services/terrain';
import { Auth } from '../../../services/auth';
import { Site } from '../../../models/site.model';
import { Terrain } from '../../../models/terrain.model';
import { TypeMatch } from '../../../models/enums.model';
import { MatchCreateRequest } from '../../../models/match.model';

@Component({
  selector: 'app-reserver',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatProgressSpinnerModule
  ],
  templateUrl: './reserver.html',
  styleUrl: './reserver.scss'
})
export class Reserver {

  readonly TypeMatch = TypeMatch;

  type: TypeMatch = TypeMatch.PRIVE;
  siteId: number | null = null;
  terrainId: number | null = null;
  dateHeure: string = '';

  sites = signal<Site[]>([]);
  terrains = signal<Terrain[]>([]);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private matchService: MatchService,
    private siteService: SiteService,
    private terrainService: TerrainService,
    private auth: Auth,
    private router: Router
  ) {
    this.siteService.getActifs().subscribe({
      next: (sites) => this.sites.set(sites)
    });
  }

  // Recharge la liste des terrains disponibles quand le site change —
  // un terrain appartient à un site précis (voir Terrain.siteId côté backend).
  onSiteChange(): void {
    this.terrainId = null;
    if (this.siteId === null) {
      this.terrains.set([]);
      return;
    }
    this.terrainService.getBySiteId(this.siteId).subscribe({
      next: (terrains) => this.terrains.set(terrains)
    });
  }

  onSubmit(): void {
    this.errorMessage.set(null);

    const organisateurId = this.auth.getMembreId();
    if (organisateurId === null || this.terrainId === null || !this.dateHeure) {
      this.errorMessage.set('Merci de remplir tous les champs.');
      return;
    }

    const request: MatchCreateRequest = {
      terrainId: this.terrainId,
      organisateurId: organisateurId,
      dateHeure: this.dateHeure,
      type: this.type
    };

    this.loading.set(true);
    this.matchService.create(request).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/membre/mes-matchs']);
      },
      error: (err) => {
        this.loading.set(false);
        // Le backend renvoie le message métier précis (solde dû, pénalité,
        // délai insuffisant, créneau occupé...) — mais RuntimeException
        // n'est pas exposée dans le body actuellement, donc message générique.
        this.errorMessage.set(
          'Réservation impossible. Vérifiez le délai minimum selon votre type de membre, votre solde, et la disponibilité du créneau.'
        );
      }
    });
  }
}