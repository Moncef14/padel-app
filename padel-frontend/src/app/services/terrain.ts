import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Terrain } from '../models/terrain.model';

const API_URL = 'http://localhost:8080/api/terrains';

@Injectable({
  providedIn: 'root'
})
export class TerrainService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<Terrain[]> {
    return this.http.get<Terrain[]>(API_URL);
  }

  getBySiteId(siteId: number): Observable<Terrain[]> {
    return this.http.get<Terrain[]>(`${API_URL}/site/${siteId}`);
  }
}