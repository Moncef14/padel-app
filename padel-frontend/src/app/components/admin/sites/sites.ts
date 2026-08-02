import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Inject } from '@angular/core';
import { SiteService } from '../../../services/site';
import { Site } from '../../../models/site.model';

@Component({
  selector: 'app-sites',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatCardModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatCheckboxModule, MatTableModule
  ],
  templateUrl: './sites.html',
  styleUrl: './sites.scss'
})
export class Sites implements OnInit {

  sites = signal<Site[]>([]);
  loading = signal(true);
  displayedColumns = ['nom', 'horaires', 'annee', 'actif', 'actions'];

  constructor(private siteService: SiteService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.chargerSites();
  }

  private chargerSites(): void {
    this.loading.set(true);
    this.siteService.getAll().subscribe({
      next: (sites) => {
        this.sites.set(sites);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  ouvrirCreation(): void {
    const dialogRef = this.dialog.open(SiteFormDialog, {
      data: null,
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(resultat => {
      if (resultat) this.chargerSites();
    });
  }

  ouvrirEdition(site: Site): void {
    const dialogRef = this.dialog.open(SiteFormDialog, {
      data: site,
      width: '450px'
    });
    dialogRef.afterClosed().subscribe(resultat => {
      if (resultat) this.chargerSites();
    });
  }

  supprimer(site: Site): void {
    if (!confirm(`Supprimer le site "${site.nom}" ?`)) return;
    this.siteService.delete(site.id).subscribe({
      next: () => this.chargerSites()
    });
  }
}

// Dialog de création/édition d'un site — combiné dans le même fichier
// pour garder le composant simple (pas besoin d'un fichier séparé pour
// un formulaire aussi court).
@Component({
  selector: 'app-site-form-dialog',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatCheckboxModule, MatButtonModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Modifier le site' : 'Nouveau site' }}</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Nom</mat-label>
        <input matInput [(ngModel)]="formulaire.nom" required>
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Heure d'ouverture</mat-label>
        <input matInput type="time" [(ngModel)]="formulaire.heureOuverture" required>
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Heure de fermeture</mat-label>
        <input matInput type="time" [(ngModel)]="formulaire.heureFermeture" required>
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Année</mat-label>
        <input matInput type="number" [(ngModel)]="formulaire.annee" required>
      </mat-form-field>
      <mat-checkbox [(ngModel)]="formulaire.actif">Site actif</mat-checkbox>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-stroked-button (click)="dialogRef.close(false)">Annuler</button>
      <button mat-flat-button color="primary" (click)="enregistrer()">Enregistrer</button>
    </mat-dialog-actions>
  `,
  styles: [`.full-width { width: 100%; margin-bottom: 4px; }`]
})
export class SiteFormDialog {

  formulaire: Partial<Site>;

  constructor(
    public dialogRef: MatDialogRef<SiteFormDialog>,
    @Inject(MAT_DIALOG_DATA) public data: Site | null,
    private siteService: SiteService
  ) {
    this.formulaire = data ? { ...data } : {
      nom: '', heureOuverture: '08:00', heureFermeture: '22:00',
      annee: new Date().getFullYear(), actif: true
    };
  }

  enregistrer(): void {
    const requete = this.data
      ? this.siteService.update(this.data.id, this.formulaire)
      : this.siteService.create(this.formulaire);

    requete.subscribe({
      next: () => this.dialogRef.close(true),
      error: () => this.dialogRef.close(false)
    });
  }
}