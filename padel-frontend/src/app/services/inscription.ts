import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InscriptionMatchResponse } from '../models/inscription.model';

const API_URL = 'http://localhost:8080/api/inscriptions';

@Injectable({
  providedIn: 'root'
})
export class InscriptionService {

  constructor(private http: HttpClient) {}

  getByMatchId(matchId: number): Observable<InscriptionMatchResponse[]> {
    return this.http.get<InscriptionMatchResponse[]>(`${API_URL}/match/${matchId}`);
  }

  getByMembreId(membreId: number): Observable<InscriptionMatchResponse[]> {
    return this.http.get<InscriptionMatchResponse[]>(`${API_URL}/membre/${membreId}`);
  }

  // Rejoindre un match public ET payer en une seule action —
  // "premier payé = premier servi" selon l'énoncé.
  inscrireEtPayer(matchId: number, membreId: number): Observable<InscriptionMatchResponse> {
    return this.http.post<InscriptionMatchResponse>(
      `${API_URL}/public/${matchId}?membreId=${membreId}`, {}
    );
  }

  // régularise une place déjà réservée (statut INSCRIT → PAYE) sur un match PRIVE — distinct de inscrireEtPayer qui fait les deux d'un coup
  payerPlace(inscriptionId: number, membreId: number): Observable<InscriptionMatchResponse> {
    return this.http.post<InscriptionMatchResponse>(
      `${API_URL}/payer/${inscriptionId}?membreId=${membreId}`, {}
    );
  }

  // pas de membreId dans l'URL/body : le backend le déduit du token JWT pour empêcher qu'on libère la place d'un autre membre
  quitterMatch(inscriptionId: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${inscriptionId}/quitter`);
  }

  // invitation par l'organisateur d'un match PRIVE — l'organisateurId est déduit du token JWT côté backend
  inviter(matchId: number, matricule: string): Observable<InscriptionMatchResponse> {
    return this.http.post<InscriptionMatchResponse>(
      `${API_URL}/inviter/${matchId}?matricule=${matricule}`, {}
    );
  }
}
