import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MembreService } from '../../../services/membre';
import { MatchService } from '../../../services/match';
import { Auth } from '../../../services/auth';
import { Router } from '@angular/router';
import { MembreResponse } from '../../../models/membre.model';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule],
  templateUrl: './profil.html',
  styleUrl: './profil.scss'
})
export class Profil implements OnInit {

  // pas de bouton "payer le solde" ici : soldeDu n'est réglable qu'au prochain paiement de place
  // (payerPlace/inscrireEtPayer y ajoutent automatiquement soldeDu, cf. InscriptionMatchService côté backend) —
  // le profil ne fait qu'afficher ce solde en lecture seule, jamais de paiement isolé
  membre = signal<MembreResponse | null>(null);
  nombreMatchs = signal<number>(0);
  loading = signal(true);
  errorMessage = signal<string | null>(null);

  constructor(
    private membreService: MembreService,
    private matchService: MatchService,
    private auth: Auth,
    private router: Router
  ) {}

  ngOnInit(): void {
    const membreId = this.auth.getMembreId();
    if (membreId === null) return;

    this.membreService.getById(membreId).subscribe({
      next: (membre) => {
        this.membre.set(membre);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });

    this.matchService.getByMembre(membreId).subscribe({
      next: (matchs) => this.nombreMatchs.set(matchs.length)
    });
  }

  supprimerCompte(): void {
    const membreId = this.auth.getMembreId();
    if (membreId === null) return;

    const confirmation = confirm(
      'Êtes-vous sûr de vouloir supprimer définitivement votre compte ? Cette action est irréversible.'
    );
    if (!confirmation) return;

    this.errorMessage.set(null);
    this.membreService.delete(membreId).subscribe({
      next: () => {
        this.auth.logout();
        this.router.navigate(['/login']);
      },
      error: () => {
        this.errorMessage.set(
          'Impossible de supprimer votre compte : vous avez des matchs actifs liés à votre profil.'
        );
      }
    });
  }
}