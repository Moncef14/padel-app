import { TypeMembre } from './enums.model';

// Login admin
export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  role: string;
  nom: string;
}

// Login membre
export interface MembreLoginRequest {
  matricule: string;
  motDePasse?: string; // optionnel — compatibilité anciens membres sans mdp
}

export interface MembreLoginResponse {
  id: number;
  token: string;
  matricule: string;
  type: string;
  nom: string;
  prenom: string;
}

// Inscription membre (self-service)
export interface RegisterRequest {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  type: TypeMembre;
  siteId: number | null;
}

export interface RegisterResponse {
  id: number;
  token: string;
  matricule: string;
  nom: string;
  prenom: string;
  type: TypeMembre;
}

// Ce qu'on stocke localement pour savoir qui est connecté
export interface AuthState {
  id: number | null;
  token: string;
  role: string | null;      // ADMIN_GLOBAL / ADMIN_SITE si admin, null si membre
  matricule: string | null; // rempli si membre connecté
  nom: string;
  prenom: string;
  siteId: number | null;
}