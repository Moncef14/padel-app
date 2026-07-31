import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatchService } from '../../../services/match';
import { InscriptionService } from '../../../services/inscription';
import { Auth } from '../../../services/auth';
import { MatchResponse } from '../../../models/match.model';
import { StatutMatch } from '../../../models/enums.model';

@Component({
  selector: 'app-matchs-publics',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule],
  templateUrl: './matchs-publics.html',
  styleUrl: './matchs-publics.scss'
})
export class MatchsPublics implements OnInit {

  matchs = signal<MatchResponse[]>([]);
  loading = signal(true);
  rejoindreEnCours = signal<number | null>(null); // id du match en cours de traitement
  errorMessage = signal<string | null>(null);

  readonly StatutMatch = StatutMatch;

  constructor(
    private matchService: MatchService,
    private inscriptionService: InscriptionService,
    private auth: Auth
  ) {}

  ngOnInit(): void {
    this.chargerMatchs();
  }

  private chargerMatchs(): void {
    this.matchService.getPublics().subscribe({
      next: (matchs) => {
        // On n'affiche que les matchs pas encore complets — sinon
        // pas de place à prendre.
        this.matchs.set(matchs.filter(m => m.statut !== StatutMatch.COMPLET
                                          && m.statut !== StatutMatch.ANNULE));
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  rejoindre(match: MatchResponse): void {
    const membreId = this.auth.getMembreId();
    if (membreId === null) return;

    this.errorMessage.set(null);
    this.rejoindreEnCours.set(match.id);

    this.inscriptionService.inscrireEtPayer(match.id, membreId).subscribe({
      next: () => {
        this.rejoindreEnCours.set(null);
        // On recharge la liste — le match rejoint peut être devenu
        // complet et doit disparaître.
        this.chargerMatchs();
      },
      error: () => {
        this.rejoindreEnCours.set(null);
        this.errorMessage.set(
          'Impossible de rejoindre ce match — il est peut-être déjà complet ou vous y êtes déjà inscrit.'
        );
      }
    });
  }

  estOrganisateur(match: MatchResponse): boolean {
    return match.organisateurId === this.auth.getMembreId();
  }
}
