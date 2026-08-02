import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialog, MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Inject } from '@angular/core';
import { TerrainService } from '../../../services/terrain';
import { SiteService } from '../../../services/site';
import { Terrain } from '../../../models/terrain.model';
import { Site } from '../../../models/site.model';

interface GroupeSite {
  nomSite: string;
  terrains: Terrain[];
}

@Component({
  selector: 'app-terrains',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule],
  templateUrl: './terrains.html',
  styleUrl: './terrains.scss'
})
export class Terrains implements OnInit {

  terrains = signal<Terrain[]>([]);
  loading = signal(true);
  errorMessage = signal<string | null>(null);

  // Regroupe les terrains par nom de site, chaque groupe trié par numéro,
  // et les groupes eux-mêmes triés par ordre alphabétique du site —
  // plus lisible qu'un tableau plat pour un admin gérant plusieurs sites.
  readonly groupesParSite = computed<GroupeSite[]>(() => {
    const groupes = new Map<string, Terrain[]>();
    for (const t of this.terrains()) {
      const nomSite = (t as any).site?.nom ?? t.nomSite ?? 'Site inconnu';
      if (!groupes.has(nomSite)) groupes.set(nomSite, []);
      groupes.get(nomSite)!.push(t);
    }
    return Array.from(groupes.entries())
      .map(([nomSite, terrains]) => ({
        nomSite,
        terrains: terrains.sort((a, b) => a.numero - b.numero)
      }))
      .sort((a, b) => a.nomSite.localeCompare(b.nomSite));
  });

  constructor(private terrainService: TerrainService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.chargerTerrains();
  }

  private chargerTerrains(): void {
    this.loading.set(true);
    this.terrainService.getAll().subscribe({
      next: (terrains) => {
        this.terrains.set(terrains);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  ouvrirCreation(): void {
    this.errorMessage.set(null);
    const dialogRef = this.dialog.open(TerrainFormDialog, { data: null, width: '400px' });
    dialogRef.afterClosed().subscribe(resultat => {
      if (resultat === true) {
        this.chargerTerrains();
      } else if (resultat === 'erreur') {
        this.errorMessage.set('Ce numéro de terrain existe déjà pour ce site.');
      }
    });
  }

  ouvrirEdition(terrain: Terrain): void {
    this.errorMessage.set(null);
    const dialogRef = this.dialog.open(TerrainFormDialog, { data: terrain, width: '400px' });
    dialogRef.afterClosed().subscribe(resultat => {
      if (resultat === true) {
        this.chargerTerrains();
      } else if (resultat === 'erreur') {
        this.errorMessage.set('Ce numéro de terrain existe déjà pour ce site.');
      }
    });
  }

  supprimer(terrain: Terrain): void {
    if (!confirm(`Supprimer le terrain ${terrain.numero} ?`)) return;
    this.errorMessage.set(null);
    this.terrainService.delete(terrain.id).subscribe({
      next: () => this.chargerTerrains(),
      error: () => {
        this.errorMessage.set(
          `Impossible de supprimer le terrain ${terrain.numero} : des matchs y sont encore rattachés.`
        );
      }
    });
  }
}

@Component({
  selector: 'app-terrain-form-dialog',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Modifier le terrain' : 'Nouveau terrain' }}</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Numéro</mat-label>
        <input matInput type="number" [(ngModel)]="formulaire.numero" required>
      </mat-form-field>
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Site</mat-label>
        <mat-select [(ngModel)]="formulaire.siteId" required>
          @for (site of sites(); track site.id) {
            <mat-option [value]="site.id">{{ site.nom }}</mat-option>
          }
        </mat-select>
      </mat-form-field>
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
export class TerrainFormDialog {

  formulaire: { numero?: number; siteId?: number };
  sites = signal<Site[]>([]);
  erreurLocale = signal<string | null>(null);

  constructor(
    public dialogRef: MatDialogRef<TerrainFormDialog>,
    @Inject(MAT_DIALOG_DATA) public data: Terrain | null,
    private terrainService: TerrainService,
    private siteService: SiteService
  ) {
    this.formulaire = data
      ? { numero: data.numero, siteId: (data as any).site?.id ?? data.siteId }
      : { numero: undefined, siteId: undefined };

    this.siteService.getAll().subscribe({
      next: (sites) => this.sites.set(sites)
    });
  }

  enregistrer(): void {
    this.erreurLocale.set(null);
    const payload = { numero: this.formulaire.numero!, site: { id: this.formulaire.siteId! } };
    const requete = this.data
      ? this.terrainService.update(this.data.id, payload)
      : this.terrainService.create(payload);

    requete.subscribe({
      next: () => this.dialogRef.close(true),
      error: () => {
        this.erreurLocale.set('Ce numéro de terrain existe déjà pour ce site.');
        this.dialogRef.close('erreur');
      }
    });
  }
}