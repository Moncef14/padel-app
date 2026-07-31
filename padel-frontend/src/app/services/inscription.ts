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

  payerPlace(inscriptionId: number, membreId: number): Observable<InscriptionMatchResponse> {
    return this.http.post<InscriptionMatchResponse>(
      `${API_URL}/payer/${inscriptionId}?membreId=${membreId}`, {}
    );
  }

  quitterMatch(inscriptionId: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${inscriptionId}/quitter`);
  }
}
