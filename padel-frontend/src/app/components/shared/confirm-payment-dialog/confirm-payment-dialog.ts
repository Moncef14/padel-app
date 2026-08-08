import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

export interface ConfirmPaymentData {
  montant: number;
}

@Component({
  selector: 'app-confirm-payment-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  templateUrl: './confirm-payment-dialog.html',
  styleUrl: './confirm-payment-dialog.scss'
})
// étape de confirmation avant payerPlace/inscrireEtPayer : le paiement débite réellement soldeDu et n'a pas de "undo" côté backend,
// donc un clic accidentel sur le bouton payer ne doit pas déclencher l'appel API directement
export class ConfirmPaymentDialog {

  constructor(
    public dialogRef: MatDialogRef<ConfirmPaymentDialog>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmPaymentData
  ) {}

  // le composant appelant attend ce booléen (via afterClosed()) pour décider s'il lance réellement l'appel au service de paiement
  confirmer(): void {
    this.dialogRef.close(true);
  }

  annuler(): void {
    this.dialogRef.close(false);
  }
}