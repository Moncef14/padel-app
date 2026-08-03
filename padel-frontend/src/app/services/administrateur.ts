import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdministrateurResponse, AdministrateurCreateRequest } from '../models/administrateur.model';

const API_URL = 'http://localhost:8080/api/admins';

@Injectable({
  providedIn: 'root'
})
export class AdministrateurService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<AdministrateurResponse[]> {
    return this.http.get<AdministrateurResponse[]>(API_URL);
  }

  create(admin: AdministrateurCreateRequest): Observable<AdministrateurResponse> {
    return this.http.post<AdministrateurResponse>(API_URL, admin);
  }

  update(id: number, admin: any): Observable<any> {
    return this.http.put(`${API_URL}/${id}`, admin);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}