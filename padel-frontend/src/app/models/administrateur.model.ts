import { RoleAdmin } from './enums.model';

export interface AdministrateurResponse {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: RoleAdmin;
  siteId: number | null;
  nomSite: string | null;
  // Pas de motDePasse ici — jamais exposé par le backend
}

export interface AdministrateurCreateRequest {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  role: RoleAdmin;
  siteId: number | null;
}