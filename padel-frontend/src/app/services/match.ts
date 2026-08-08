import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatchResponse, MatchCreateRequest } from '../models/match.model';
import { StatutMatch, TypeMatch } from '../models/enums.model';

const API_URL = 'http://localhost:8080/api/matchs';

@Injectable({
  providedIn: 'root'
})
export class MatchService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<MatchResponse[]> {
    return this.http.get<MatchResponse[]>(API_URL);
  }

  // matchs de type PUBLIC uniquement — n'inclut pas les PRIVE basculés DEVENU_PUBLIC (ceux-là restent accessibles via getAll)
  getPublics(): Observable<MatchResponse[]> {
    return this.http.get<MatchResponse[]>(`${API_URL}/publics`);
  }

  // historique d'un membre : matchs organisés OU rejoints (cf. requête JPQL findByMembreParticipant côté backend)
  getByMembre(membreId: number): Observable<MatchResponse[]> {
    return this.http.get<MatchResponse[]>(`${API_URL}/membre/${membreId}`);
  }

  getById(id: number): Observable<MatchResponse> {
    return this.http.get<MatchResponse>(`${API_URL}/${id}`);
  }

  create(request: MatchCreateRequest): Observable<MatchResponse> {
    return this.http.post<MatchResponse>(API_URL, request);
  }

  // pas de body : le demandeur est identifié côté backend via le token JWT (organisateur ou admin), pas par un id envoyé ici
  annuler(id: number): Observable<MatchResponse> {
    return this.http.put<MatchResponse>(`${API_URL}/${id}/annuler`, {});
  }
}