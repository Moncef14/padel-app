import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatchService } from '../../../services/match';
import { MatchResponse } from '../../../models/match.model';
import { StatutMatch } from '../../../models/enums.model';

@Component({
  selector: 'app-matchs',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatTableModule],
  templateUrl: './matchs.html',
  styleUrl: './matchs.scss'
})
export class Matchs implements OnInit {

  matchs = signal<MatchResponse[]>([]);
  loading = signal(true);
  errorMessage = signal<string | null>(null);
  displayedColumns = ['dateHeure', 'site', 'organisateur', 'type', 'statut', 'montant', 'actions'];

  readonly StatutMatch = StatutMatch;

  constructor(private matchService: MatchService) {}

  ngOnInit(): void {
    this.chargerMatchs();
  }

  private chargerMatchs(): void {
    this.loading.set(true);
    this.matchService.getAll().subscribe({
      next: (matchs) => {
        // Tri par date décroissante — les matchs les plus récents/à venir
        // en premier, plus pertinent pour un admin qui supervise.
        this.matchs.set(matchs.sort((a, b) =>
          new Date(b.dateHeure).getTime() - new Date(a.dateHeure).getTime()));
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  annuler(match: MatchResponse): void {
    if (!confirm(`Annuler le match du ${match.dateHeure} ?`)) return;
    this.errorMessage.set(null);
    this.matchService.annuler(match.id).subscribe({
      next: () => this.chargerMatchs(),
      error: () => this.errorMessage.set('Impossible d\'annuler ce match.')
    });
  }
}