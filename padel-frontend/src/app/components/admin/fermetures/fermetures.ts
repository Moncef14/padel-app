import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, provideNativeDateAdapter } from '@angular/material/core';
import { MatDialog, MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Inject } from '@angular/core';
import { JourFermetureService } from '../../../services/jour-fermeture';
import { SiteService } from '../../../services/site';
import { JourFermeture } from '../../../models/jour-fermeture.model';
import { Site } from '../../../models/site.model';

@Component({
  selector: 'app-fermetures',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatTableModule],
  templateUrl: './fermetures.html',
  styleUrl: './fermetures.scss'
})
export class Fermetures implements OnInit {

  fermetures = signal<JourFermeture[]>([]);
  sitesMap = signal<Map<number, string>>(new Map());
  loading = signal(true);
  displayedColumns = ['date', 'portee', 'actions'];

  constructor(
    private fermetureService: JourFermetureService,
    private siteService: SiteService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.siteService.getAll().subscribe({
      next: (sites) => {
        const map = new Map<number, string>();
        sites.forEach(s => map.set(s.id, s.nom));
        this.sitesMap.set(map);
        this.chargerFermetures();
      }
    });
  }

  private chargerFermetures(): void {
    this.loading.set(true);
    this.fermetureService.getAll().subscribe({
      next: (fermetures) => {
        this.fermetures.set(
          fermetures.sort((a, b) => a.date.localeCompare(b.date))
        );
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  nomSite(fermeture: JourFermeture): string {
    const site = (fermeture as any).site;
    if (!site) return 'Fermeture globale';
    return site.nom ?? 'Site inconnu';
  }

  ouvrirCreation(): void {
    const dialogRef = this.dialog.open(FermetureFormDialog, { width: '400px' });
    dialogRef.afterClosed().subscribe(resultat => {
      if (resultat) this.chargerFermetures();
    });
  }

  supprimer(fermeture: JourFermeture): void {
    if (!confirm(`Supprimer cette fermeture du ${fermeture.date} ?`)) return;
    this.fermetureService.delete(fermeture.id).subscribe({
      next: () => this.chargerFermetures()
    });
  }
}

@Component({
  selector: 'app-fermeture-form-dialog',
  standalone: true,
  providers: [provideNativeDateAdapter()],
  imports: [
    CommonModule, FormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatDatepickerModule, MatNativeDateModule, MatButtonModule
  ],
  template: `
    <h2 mat-dialog-title>Nouvelle fermeture</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Date</mat-label>
        <input matInput [matDatepicker]="picker" [(ngModel)]="date" required>
        <mat-datepicker-toggle matIconSuffix [for]="picker"></mat-datepicker-toggle>
        <mat-datepicker #picker></mat-datepicker>
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Portée</mat-label>
        <mat-select [(ngModel)]="siteId">
          <mat-option [value]="null">Fermeture globale (tous les sites)</mat-option>
          @for (site of sites(); track site.id) {
            <mat-option [value]="site.id">{{ site.nom }}</mat-option>
          }
        </mat-select>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-stroked-button (click)="dialogRef.close(false)">Annuler</button>
      <button mat-flat-button color="primary" (click)="enregistrer()">Enregistrer</button>
    </mat-dialog-actions>
  `,
  styles: [`.full-width { width: 100%; margin-bottom: 4px; }`]
})
export class FermetureFormDialog {

  date: Date | null = null;
  siteId: number | null = null;
  sites = signal<Site[]>([]);

  constructor(
    public dialogRef: MatDialogRef<FermetureFormDialog>,
    private fermetureService: JourFermetureService,
    private siteService: SiteService
  ) {
    this.siteService.getAll().subscribe({
      next: (sites) => this.sites.set(sites)
    });
  }

  private formatDateLocale(d: Date): string {
    const annee = d.getFullYear();
    const mois = (d.getMonth() + 1).toString().padStart(2, '0');
    const jour = d.getDate().toString().padStart(2, '0');
    return `${annee}-${mois}-${jour}`;
  }

  enregistrer(): void {
    if (!this.date) return;
    const payload = {
      date: this.formatDateLocale(this.date),
      site: this.siteId !== null ? { id: this.siteId } : null
    };
    this.fermetureService.create(payload).subscribe({
      next: () => this.dialogRef.close(true),
      error: () => this.dialogRef.close(false)
    });
  }
}