import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardStats, StatsParSite } from '../models/stats.model';

const API_URL = 'http://localhost:8080/api/stats';

@Injectable({
  providedIn: 'root'
})
export class StatsService {

  constructor(private http: HttpClient) {}

  // périmètre (global ou limité au site) déterminé côté backend depuis le rôle dans le token, pas de paramètre à passer ici
  getDashboard(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${API_URL}/dashboard`);
  }

  // réservé ADMIN_GLOBAL côté backend (@PreAuthorize) : comparaison entre sites, sans objet pour un ADMIN_SITE
  getStatsParSite(): Observable<StatsParSite[]> {
    return this.http.get<StatsParSite[]>(`${API_URL}/par-site`);
  }
}