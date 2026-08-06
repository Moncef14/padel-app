import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatchService } from '../../../services/match';
import { SiteService } from '../../../services/site';
import { TerrainService } from '../../../services/terrain';
import { Auth } from '../../../services/auth';
import { Site } from '../../../models/site.model';
import { Terrain } from '../../../models/terrain.model';
import { TypeMatch, StatutMatch } from '../../../models/enums.model';
import { MatchCreateRequest, MatchResponse } from '../../../models/match.model';

interface Creneau {
  heure: string;      // "10:00"
  disponible: boolean;
}

@Component({
  selector: 'app-reserver',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatSelectModule, MatButtonModule, MatProgressSpinnerModule,
    MatDatepickerModule, MatNativeDateModule
  ],
  templateUrl: './reserver.html',
  styleUrl: './reserver.scss'
})
export class Reserver {

  readonly typeMatch = TypeMatch;

  type: TypeMatch = TypeMatch.PRIVE;
  siteId: number | null = null;
  terrainId: number | null = null;
  date: Date | null = null;
  heureChoisie: string | null = null;

  sites = signal<Site[]>([]);
  terrains = signal<Terrain[]>([]);
  creneaux = signal<Creneau[]>([]);
  loadingCreneaux = signal(false);
  loading = signal(false);
  errorMessage = signal<string | null>(null);

  private siteSelectionne: Site | null = null;

  // Date minimum autorisée selon le type de membre — reflète les délais
  // du backend (MatchService.create() : 21j GLOBAL, 14j SITE, 5j LIBRE).
  readonly dateMin = computed(() => {
    const type = this.auth.getMembreType();
    const delaiJours = type === 'GLOBAL' ? 21 : type === 'SITE' ? 14 : 5;
    const min = new Date();
    min.setDate(min.getDate() + delaiJours);
    return min;
  });

  readonly siteVerrouille = computed(() => this.auth.getMembreType() === 'SITE');

  // Grise dans le datepicker les dates antérieures au délai minimum du membre.
  dateFilter = (d: Date | null): boolean => {
    if (!d) return false;
    const min = new Date(this.dateMin());
    min.setHours(0, 0, 0, 0);
    const date = new Date(d);
    date.setHours(0, 0, 0, 0);
    return date >= min;
  };

  constructor(
    private matchService: MatchService,
    private siteService: SiteService,
    private terrainService: TerrainService,
    private auth: Auth,
    private router: Router
  ) {
    this.siteService.getActifs().subscribe({
      next: (sites) => {
        this.sites.set(sites);
        const membreType = this.auth.getMembreType();
        const membreSiteId = this.auth.getMembreSiteId();
        if (membreType === 'SITE' && membreSiteId !== null) {
          this.siteId = membreSiteId;
          this.onSiteChange();
        }
      }
    });
  }

  onSiteChange(): void {
    this.terrainId = null;
    this.creneaux.set([]);
    this.siteSelectionne = this.sites().find(s => s.id === this.siteId) ?? null;
    if (this.siteId === null) {
      this.terrains.set([]);
      return;
    }
    this.terrainService.getBySiteId(this.siteId).subscribe({
      next: (terrains) => this.terrains.set(terrains)
    });
  }

  onTerrainOuDateChange(): void {
    this.heureChoisie = null;
    if (this.terrainId === null || this.date === null || !this.siteSelectionne) {
      this.creneaux.set([]);
      return;
    }
    this.genererCreneaux();
  }

  // Génère les créneaux horaires possibles pour le site (basés sur ses
  // horaires d'ouverture/fermeture, espacés de 1h45 comme côté backend),
  // puis marque comme indisponibles ceux qui chevauchent un match existant
  // sur ce terrain à cette date.
  private genererCreneaux(): void {
    this.loadingCreneaux.set(true);
    const dateStr = this.formatDateLocale(this.date!);
    this.matchService.getAll().subscribe({
      next: (matchs) => {
        const matchsOccupes = matchs.filter(m =>
          m.terrainId === this.terrainId &&
          m.statut !== StatutMatch.ANNULE &&
          m.dateHeure.startsWith(dateStr)
        );

        const ouverture = this.siteSelectionne!.heureOuverture.substring(0, 5);
        const fermeture = this.siteSelectionne!.heureFermeture.substring(0, 5);
        const liste: Creneau[] = [];

        let [h, m] = ouverture.split(':').map(Number);
        const [hFin, mFin] = fermeture.split(':').map(Number);

        while (h < hFin || (h === hFin && m < mFin)) {
          const heureStr = `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}`;
          const dejaPris = matchsOccupes.some(match => {
            const heureMatch = match.dateHeure.substring(11, 16);
            return heureMatch === heureStr;
          });
          liste.push({ heure: heureStr, disponible: !dejaPris });

          m += 105; // 1h45 entre chaque créneau
          h += Math.floor(m / 60);
          m = m % 60;
        }

        this.creneaux.set(liste);
        this.loadingCreneaux.set(false);
      },
      error: () => this.loadingCreneaux.set(false)
    });
  }

  private formatDateLocale(d: Date): string {
    const annee = d.getFullYear();
    const mois = (d.getMonth() + 1).toString().padStart(2, '0');
    const jour = d.getDate().toString().padStart(2, '0');
    return `${annee}-${mois}-${jour}`;
  }

  choisirCreneau(creneau: Creneau): void {
    if (!creneau.disponible) return;
    this.heureChoisie = creneau.heure;
  }

  onSubmit(): void {
    this.errorMessage.set(null);

    const organisateurId = this.auth.getMembreId();
    if (organisateurId === null || this.terrainId === null || this.date === null || !this.heureChoisie) {
      this.errorMessage.set('Merci de sélectionner un créneau.');
      return;
    }

    const dateStr = this.formatDateLocale(this.date!);
    const request: MatchCreateRequest = {
      terrainId: this.terrainId,
      organisateurId: organisateurId,
      dateHeure: `${dateStr}T${this.heureChoisie}:00`,
      type: this.type
    };

    this.loading.set(true);
    this.matchService.create(request).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/membre/mes-matchs']);
      },
      error: () => {
        this.loading.set(false);
        this.errorMessage.set(
          'Réservation impossible. Vérifiez votre solde, une éventuelle pénalité, ou que ce site accepte votre type de membre.'
        );
      }
    });
  }
}
