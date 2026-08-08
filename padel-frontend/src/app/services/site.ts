import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Site } from '../models/site.model';

const API_URL = 'http://localhost:8080/api/sites';

@Injectable({
  providedIn: 'root'
})
export class SiteService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<Site[]> {
    return this.http.get<Site[]>(API_URL);
  }

  // route publique côté backend (permitAll) : utilisée sur les écrans login/inscription, avant qu'un token existe
  getActifs(): Observable<Site[]> {
    return this.http.get<Site[]>(`${API_URL}/actifs`);
  }

  getById(id: number): Observable<Site> {
    return this.http.get<Site>(`${API_URL}/${id}`);
  }

  create(site: Partial<Site>): Observable<Site> {
    return this.http.post<Site>(API_URL, site);
  }

  update(id: number, site: Partial<Site>): Observable<Site> {
    return this.http.put<Site>(`${API_URL}/${id}`, site);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}