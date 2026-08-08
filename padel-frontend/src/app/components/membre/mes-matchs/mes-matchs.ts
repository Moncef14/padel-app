import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatchService } from '../../../services/match';
import { Auth } from '../../../services/auth';
import { MatchResponse } from '../../../models/match.model';
import { StatutMatch, TypeMatch } from '../../../models/enums.model';

@Component({
  selector: 'app-mes-matchs',
  standalone: true,
  imports: [CommonModule, RouterLink, MatCardModule, MatButtonModule, MatChipsModule, MatTabsModule],
  templateUrl: './mes-matchs.html',
  styleUrl: './mes-matchs.scss'
})
export class MesMatchs implements OnInit {

  matchs = signal<MatchResponse[]>([]);
  loading = signal(true);

  // "actif" = pas encore joué et pas annulé ; un match COMPLET ou DEVENU_PUBLIC reste actif tant que sa date n'est pas passée
  readonly matchsActifs = computed(() =>
    this.matchs().filter(m =>
      m.statut !== StatutMatch.ANNULE && new Date(m.dateHeure) >= new Date()
    )
  );

  // basé sur le statut ET la date (pas uniquement le statut) : un match passé mais jamais annulé doit quand même sortir de la liste active
  readonly matchsHistorique = computed(() =>
    this.matchs().filter(m =>
      m.statut === StatutMatch.ANNULE || new Date(m.dateHeure) < new Date()
    )
  );

  // Exposés au template pour les comparaisons de statut/type
  readonly statutMatch = StatutMatch;
  readonly typeMatch = TypeMatch;

  constructor(private matchService: MatchService, private auth: Auth) {}

  ngOnInit(): void {
    const membreId = this.auth.getMembreId();
    if (membreId === null) return;

    this.matchService.getByMembre(membreId).subscribe({
      next: (matchs) => {
        this.matchs.set(matchs);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  // Traduit le statut technique en libellé lisible pour l'affichage
  libelleStatut(statut: StatutMatch): string {
    const libelles: Record<StatutMatch, string> = {
      [StatutMatch.EN_ATTENTE]: 'En attente',
      [StatutMatch.COMPLET]: 'Complet',
      [StatutMatch.ANNULE]: 'Annulé',
      [StatutMatch.DEVENU_PUBLIC]: 'Devenu public'
    };
    return libelles[statut];
  }

  // Détermine si l'utilisateur connecté est l'organisateur de ce match —
  // utile pour savoir si le bouton "Annuler" doit être proposé.
  estOrganisateur(match: MatchResponse): boolean {
    return match.organisateurId === this.auth.getMembreId();
  }
}