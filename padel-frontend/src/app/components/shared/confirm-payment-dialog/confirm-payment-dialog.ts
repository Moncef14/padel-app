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
export class ConfirmPaymentDialog {

  constructor(
    public dialogRef: MatDialogRef<ConfirmPaymentDialog>,
    @Inject(MAT_DIALOG_DATA) public data: ConfirmPaymentData
  ) {}

  confirmer(): void {
    this.dialogRef.close(true);
  }

  annuler(): void {
    this.dialogRef.close(false);
  }
}