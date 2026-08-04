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

  getDashboard(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${API_URL}/dashboard`);
  }

  getStatsParSite(): Observable<StatsParSite[]> {
    return this.http.get<StatsParSite[]>(`${API_URL}/par-site`);
  }
}