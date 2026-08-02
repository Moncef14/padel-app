import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JourFermeture } from '../models/jour-fermeture.model';

const API_URL = 'http://localhost:8080/api/fermetures';

@Injectable({
  providedIn: 'root'
})
export class JourFermetureService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<JourFermeture[]> {
    return this.http.get<JourFermeture[]>(API_URL);
  }

  getGlobaux(): Observable<JourFermeture[]> {
    return this.http.get<JourFermeture[]>(`${API_URL}/globaux`);
  }

  getBySiteId(siteId: number): Observable<JourFermeture[]> {
    return this.http.get<JourFermeture[]>(`${API_URL}/site/${siteId}`);
  }

  create(fermeture: { date: string; site: { id: number } | null }): Observable<JourFermeture> {
    return this.http.post<JourFermeture>(API_URL, fermeture);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}