import { TypeMembre } from './enums.model';

// Correspond à MembreResponse.java — ce que le backend retourne
export interface MembreResponse {
  id: number;
  matricule: string;
  nom: string;
  prenom: string;
  email: string;
  type: TypeMembre;
  siteId: number | null;
  nomSite: string | null;
  soldeDu: number;
  penaliteJusquAu: string | null; // LocalDate → string ISO
}

// Correspond à MembreCreateRequest.java — ce qu'on envoie pour créer un membre (admin)
export interface MembreCreateRequest {
  matricule: string;
  nom: string;
  prenom: string;
  email: string;
  type: TypeMembre;
  siteId: number | null;
  soldeDu?: number;
}