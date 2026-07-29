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

  getPublics(): Observable<MatchResponse[]> {
    return this.http.get<MatchResponse[]>(`${API_URL}/publics`);
  }

  getByMembre(membreId: number): Observable<MatchResponse[]> {
    return this.http.get<MatchResponse[]>(`${API_URL}/membre/${membreId}`);
  }

  getById(id: number): Observable<MatchResponse> {
    return this.http.get<MatchResponse>(`${API_URL}/${id}`);
  }

  create(request: MatchCreateRequest): Observable<MatchResponse> {
    return this.http.post<MatchResponse>(API_URL, request);
  }

  annuler(id: number): Observable<MatchResponse> {
    return this.http.put<MatchResponse>(`${API_URL}/${id}/annuler`, {});
  }
}