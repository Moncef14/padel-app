import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDialog } from '@angular/material/dialog';
import { MatchService } from '../../../services/match';
import { InscriptionService } from '../../../services/inscription';
import { MembreService } from '../../../services/membre';
import { Auth } from '../../../services/auth';
import { MatchResponse } from '../../../models/match.model';
import { InscriptionMatchResponse } from '../../../models/inscription.model';
import { MembreResponse } from '../../../models/membre.model';
import { StatutPaiement } from '../../../models/enums.model';
import { ConfirmPaymentDialog } from '../../shared/confirm-payment-dialog/confirm-payment-dialog';
import { NotificationService } from '../../../services/notification';

@Component({
  selector: 'app-match-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, MatCardModule, MatButtonModule, MatFormFieldModule, MatSelectModule],
  templateUrl: './match-detail.html',
  styleUrl: './match-detail.scss'
})
export class MatchDetail implements OnInit {

  match = signal<MatchResponse | null>(null);
  inscriptions = signal<InscriptionMatchResponse[]>([]);
  loading = signal(true);
  actionEnCours = signal(false);
  errorMessage = signal<string | null>(null);
  membresDisponibles = signal<MembreResponse[]>([]);
  matriculeSelectionne: string | null = null;

  readonly statutPaiement = StatutPaiement;

  private matchId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private matchService: MatchService,
    private inscriptionService: InscriptionService,
    private membreService: MembreService,
    public auth: Auth,
    private dialog: MatDialog,
    private notification: NotificationService
  ) {}

  ngOnInit(): void {
    this.matchId = Number(this.route.snapshot.paramMap.get('id'));
    this.chargerDonnees();
  }

  private chargerDonnees(): void {
    this.loading.set(true);
    this.matchService.getById(this.matchId).subscribe({
      next: (match) => {
        this.match.set(match);
        this.inscriptionService.getByMatchId(this.matchId).subscribe({
          next: (inscriptions) => {
            this.inscriptions.set(inscriptions);
            this.loading.set(false);
            this.chargerMembresDisponibles();
          },
          error: () => this.loading.set(false)
        });
      },
      error: () => this.loading.set(false)
    });
  }

  // charge tous les membres pour peupler le select d'invitation, en excluant ceux déjà inscrits à ce match
  private chargerMembresDisponibles(): void {
    this.membreService.getAll().subscribe({
      next: (membres) => {
        const matriculesInscrits = this.inscriptions().map(i => i.matriculeMembre);
        this.membresDisponibles.set(
          membres.filter(m => !matriculesInscrits.includes(m.matricule))
        );
      }
    });
  }

  // L'inscription du membre connecté sur ce match, si elle existe.
  monInscription(): InscriptionMatchResponse | undefined {
    return this.inscriptions().find(i => i.membreId === this.auth.getMembreId());
  }

  estOrganisateur(): boolean {
    return this.match()?.organisateurId === this.auth.getMembreId();
  }

  // l'organisateur est toujours PAYE dès la création du match (cf. MatchService.create côté backend) : il n'a donc jamais
  // le statut INSCRIT et ce bouton ne peut logiquement lui être proposé, pas besoin de l'exclure explicitement ici
  peutPayer(): boolean {
    const inscription = this.monInscription();
    return inscription !== undefined && inscription.statutPaiement === StatutPaiement.INSCRIT;
  }

  // exclusion explicite de l'organisateur : lui seul peut annuler le match entier (cf. annuler()), il ne peut pas
  // se désister individuellement comme un joueur invité (règle backend, InscriptionMatchService.quitterMatch)
  peutQuitter(): boolean {
    const inscription = this.monInscription();
    return inscription !== undefined
        && inscription.statutPaiement === StatutPaiement.PAYE
        && !this.estOrganisateur();
  }

  // visible seulement si organisateur, match PRIVE, et pas complet
  peutInviter(): boolean {
    const m = this.match();
    return m !== null
        && this.estOrganisateur()
        && m.type === 'PRIVE'
        && m.statut !== 'COMPLET'
        && m.statut !== 'ANNULE';
  }

  inviter(): void {
    if (!this.matriculeSelectionne) return;
    this.actionEnCours.set(true);
    this.errorMessage.set(null);
    this.inscriptionService.inviter(this.matchId, this.matriculeSelectionne).subscribe({
      next: () => {
        this.actionEnCours.set(false);
        this.matriculeSelectionne = null;
        this.notification.succes('Joueur invité avec succès !');
        this.chargerDonnees();
      },
      error: (err) => {
        this.actionEnCours.set(false);
        const message = err.error?.erreur || 'Impossible d\'inviter ce joueur.';
        this.errorMessage.set(message);
        this.notification.erreur(message);
      }
    });
  }

  payer(): void {
    const inscription = this.monInscription();
    const membreId = this.auth.getMembreId();
    if (!inscription || membreId === null) return;

    const dialogRef = this.dialog.open(ConfirmPaymentDialog, {
      data: { montant: 15 },
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(confirme => {
      if (!confirme) return;

      this.actionEnCours.set(true);
      this.errorMessage.set(null);
      this.inscriptionService.payerPlace(inscription.id, membreId).subscribe({
        next: () => {
          this.actionEnCours.set(false);
          this.chargerDonnees();
          this.notification.succes('Paiement effectué avec succès !');
        },
        error: () => {
          this.actionEnCours.set(false);
          this.errorMessage.set('Le paiement a échoué. Réessayez plus tard.');
        }
      });
    });
  }

  quitter(): void {
    const inscription = this.monInscription();
    if (!inscription) return;

    this.actionEnCours.set(true);
    this.errorMessage.set(null);
    this.inscriptionService.quitterMatch(inscription.id).subscribe({
      next: () => {
        this.actionEnCours.set(false);
        this.notification.succes('Vous avez quitté le match.');
        this.router.navigate(['/membre/mes-matchs']);
      },
      error: () => {
        this.actionEnCours.set(false);
        this.errorMessage.set('Impossible de quitter ce match.');
      }
    });
  }

  annuler(): void {
    this.actionEnCours.set(true);
    this.errorMessage.set(null);
    this.matchService.annuler(this.matchId).subscribe({
      next: () => {
        this.actionEnCours.set(false);
        this.chargerDonnees();
        this.notification.succes('Match annulé avec succès.');
      },
      error: () => {
        this.actionEnCours.set(false);
        this.errorMessage.set('Impossible d\'annuler ce match.');
      }
    });
  }
}