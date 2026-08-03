import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private snackBar: MatSnackBar) {}

  succes(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      panelClass: ['notification-succes'],
      horizontalPosition: 'center',
      verticalPosition: 'bottom'
    });
  }

  erreur(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      panelClass: ['notification-erreur'],
      horizontalPosition: 'center',
      verticalPosition: 'bottom'
    });
  }
}