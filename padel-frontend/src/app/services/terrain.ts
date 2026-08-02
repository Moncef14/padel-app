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

  create(terrain: { numero: number; site: { id: number } }): Observable<Terrain> {
    return this.http.post<Terrain>(API_URL, terrain);
  }

  update(id: number, terrain: { numero: number; site: { id: number } }): Observable<Terrain> {
    return this.http.put<Terrain>(`${API_URL}/${id}`, terrain);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}