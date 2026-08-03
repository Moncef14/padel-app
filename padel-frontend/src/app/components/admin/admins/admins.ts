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
import { AdministrateurService } from '../../../services/administrateur';
import { SiteService } from '../../../services/site';
import { AdministrateurResponse } from '../../../models/administrateur.model';
import { RoleAdmin } from '../../../models/enums.model';
import { Site } from '../../../models/site.model';

@Component({
  selector: 'app-admins',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatTableModule],
  templateUrl: './admins.html',
  styleUrl: './admins.scss'
})
export class Admins implements OnInit {

  admins = signal<AdministrateurResponse[]>([]);
  loading = signal(true);
  errorMessage = signal<string | null>(null);
  displayedColumns = ['nom', 'email', 'role', 'site', 'actions'];

  constructor(private adminService: AdministrateurService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.chargerAdmins();
  }

  private chargerAdmins(): void {
    this.loading.set(true);
    this.adminService.getAll().subscribe({
      next: (admins) => {
        this.admins.set(admins);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  ouvrirCreation(): void {
    this.errorMessage.set(null);
    const dialogRef = this.dialog.open(AdminFormDialog, { data: null, width: '450px' });
    dialogRef.afterClosed().subscribe(resultat => {
      if (resultat) this.chargerAdmins();
    });
  }

  ouvrirEdition(admin: AdministrateurResponse): void {
    this.errorMessage.set(null);
    const dialogRef = this.dialog.open(AdminFormDialog, { data: admin, width: '450px' });
    dialogRef.afterClosed().subscribe(resultat => {
      if (resultat) this.chargerAdmins();
    });
  }

  supprimer(admin: AdministrateurResponse): void {
    if (!confirm(`Supprimer l'administrateur ${admin.prenom} ${admin.nom} ?`)) return;
    this.errorMessage.set(null);
    this.adminService.delete(admin.id).subscribe({
      next: () => this.chargerAdmins(),
      error: () => this.errorMessage.set('Impossible de supprimer cet administrateur.')
    });
  }
}

@Component({
  selector: 'app-admin-form-dialog',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule
  ],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Modifier l\\'administrateur' : 'Nouvel administrateur' }}</h2>
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
               pattern="^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$" required #emailField="ngModel">
        @if (emailField.invalid && emailField.touched) {
          <mat-error>Email invalide</mat-error>
        }
      </mat-form-field>
      @if (!data) {
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Mot de passe</mat-label>
          <input matInput type="password" [(ngModel)]="formulaire.motDePasse" required>
        </mat-form-field>
      }
      <mat-form-field appearance="outline" class="full-width">
        <mat-label>Rôle</mat-label>
        <mat-select [(ngModel)]="formulaire.role" required>
          <mat-option [value]="RoleAdmin.ADMIN_GLOBAL">Admin Global</mat-option>
          <mat-option [value]="RoleAdmin.ADMIN_SITE">Admin Site</mat-option>
        </mat-select>
      </mat-form-field>
      @if (formulaire.role === RoleAdmin.ADMIN_SITE) {
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
export class AdminFormDialog {

  readonly RoleAdmin = RoleAdmin;

  formulaire: any;
  sites = signal<Site[]>([]);
  erreurLocale = signal<string | null>(null);

  constructor(
    public dialogRef: MatDialogRef<AdminFormDialog>,
    @Inject(MAT_DIALOG_DATA) public data: AdministrateurResponse | null,
    private adminService: AdministrateurService,
    private siteService: SiteService
  ) {
    this.formulaire = data
      ? { ...data }
      : { prenom: '', nom: '', email: '', motDePasse: '', role: RoleAdmin.ADMIN_SITE, siteId: null };

    this.siteService.getAll().subscribe({
      next: (sites) => this.sites.set(sites)
    });
  }

  enregistrer(): void {
    this.erreurLocale.set(null);

    if (this.formulaire.role !== RoleAdmin.ADMIN_SITE) {
      this.formulaire.siteId = null;
    }

    const requete = this.data
      ? this.adminService.update(this.data.id, this.formulaire)
      : this.adminService.create(this.formulaire);

    requete.subscribe({
      next: () => this.dialogRef.close(true),
      error: () => this.erreurLocale.set('Email invalide ou déjà existant.')
    });
  }
}