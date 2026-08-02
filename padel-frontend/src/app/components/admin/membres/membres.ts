import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialog, MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Inject } from '@angular/core';
import { MembreService } from '../../../services/membre';
import { SiteService } from '../../../services/site';
import { MembreResponse } from '../../../models/membre.model';
import { TypeMembre } from '../../../models/enums.model';
import { Site } from '../../../models/site.model';

@Component({
  selector: 'app-membres',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatTableModule],
  templateUrl: './membres.html',
  styleUrl: './membres.scss'
})
export class Membres implements OnInit {

  membres = signal<MembreResponse[]>([]);
  loading = signal(true);
  errorMessage = signal<string | null>(null);
  displayedColumns = ['matricule', 'nom', 'type', 'site', 'soldeDu', 'penalite', 'actions'];

  constructor(private membreService: MembreService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.chargerMembres();
  }

  private chargerMembres(): void {
    this.loading.set(true);
    this.membreService.getAll().subscribe({
      next: (membres) => {
        this.membres.set(membres);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  ouvrirCreation(): void {
    this.errorMessage.set(null);
    const dialogRef = this.dialog.open(MembreFormDialog, { data: null, width: '450px' });
    dialogRef.afterClosed().subscribe(resultat => {
      if (resultat) this.chargerMembres();
    });
  }

  ouvrirEdition(membre: MembreResponse): void {
    this.errorMessage.set(null);
    const dialogRef = this.dialog.open(MembreFormDialog, { data: membre, width: '450px' });
    dialogRef.afterClosed().subscribe(resultat => {
      if (resultat) this.chargerMembres();
    });
  }

  supprimer(membre: MembreResponse): void {
    if (!confirm(`Supprimer le membre ${membre.prenom} ${membre.nom} ?`)) return;
    this.errorMessage.set(null);
    this.membreService.delete(membre.id).subscribe({
      next: () => this.chargerMembres(),
      error: () => this.errorMessage.set(
        `Impossible de supprimer ce membre : il a peut-être des matchs ou inscriptions liés.`
      )
    });
  }
}

@Component({
  selector: 'app-membre-form-dialog',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Modifier le membre' : 'Nouveau membre' }}</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Prénom</mat-label>
        <input matInput [(ngModel)]="formulaire.prenom" required>
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Nom</mat-label>
        <input matInput [(ngModel)]="formulaire.nom" required>
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Email</mat-label>
        <input matInput type="email" [(ngModel)]="formulaire.email"
               pattern="^[^\s@]+@[^\s@]+\.[^\s@]+$" required #emailField="ngModel">
        @if (emailField.invalid && emailField.touched) {
          <mat-error>Email invalide</mat-error>
        }
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Type</mat-label>
        <mat-select [(ngModel)]="formulaire.type" required>
          <mat-option [value]="TypeMembre.GLOBAL">Global</mat-option>
          <mat-option [value]="TypeMembre.SITE">Site</mat-option>
          <mat-option [value]="TypeMembre.LIBRE">Libre</mat-option>
        </mat-select>
      </mat-form-field>
      @if (formulaire.type === TypeMembre.SITE) {
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Site</mat-label>
          <mat-select [(ngModel)]="formulaire.siteId" required>
            @for (site of sites(); track site.id) {
              <mat-option [value]="site.id">{{ site.nom }}</mat-option>
            }
          </mat-select>
        </mat-form-field>
      }
      @if (erreurLocale()) {
        <p class="dialog-error">{{ erreurLocale() }}</p>
      }
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-stroked-button (click)="dialogRef.close(false)">Annuler</button>
      <button mat-flat-button color="primary" (click)="enregistrer()">Enregistrer</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .full-width { width: 100%; margin-bottom: 4px; }
    .dialog-error { color: #C62828; font-size: 13px; margin: 4px 0 0 0; }
  `]
})
export class MembreFormDialog {

  readonly TypeMembre = TypeMembre;

  formulaire: any;
  sites = signal<Site[]>([]);
  erreurLocale = signal<string | null>(null);

  constructor(
    public dialogRef: MatDialogRef<MembreFormDialog>,
    @Inject(MAT_DIALOG_DATA) public data: MembreResponse | null,
    private membreService: MembreService,
    private siteService: SiteService
  ) {
    this.formulaire = data
      ? { ...data }
      : { prenom: '', nom: '', email: '', type: TypeMembre.LIBRE, siteId: null };

    this.siteService.getAll().subscribe({
      next: (sites) => this.sites.set(sites)
    });
  }

  enregistrer(): void {
    this.erreurLocale.set(null);

    if (this.formulaire.type !== TypeMembre.SITE) {
      this.formulaire.siteId = null;
    }

    const requete = this.data
      ? this.membreService.update(this.data.id, this.formulaire)
      : this.membreService.create(this.formulaire);

    requete.subscribe({
      next: () => this.dialogRef.close(true),
      error: () => this.erreurLocale.set('Email invalide ou déjà existant.')
    });
  }
}