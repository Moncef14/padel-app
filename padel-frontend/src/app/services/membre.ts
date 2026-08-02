import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MembreResponse, MembreCreateRequest } from '../models/membre.model';

const API_URL = 'http://localhost:8080/api/membres';

@Injectable({
  providedIn: 'root'
})
export class MembreService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<MembreResponse[]> {
    return this.http.get<MembreResponse[]>(API_URL);
  }

  getById(id: number): Observable<MembreResponse> {
    return this.http.get<MembreResponse>(`${API_URL}/${id}`);
  }

  create(membre: MembreCreateRequest): Observable<MembreResponse> {
    return this.http.post<MembreResponse>(API_URL, membre);
  }

  update(id: number, membre: any): Observable<any> {
    return this.http.put(`${API_URL}/${id}`, membre);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}