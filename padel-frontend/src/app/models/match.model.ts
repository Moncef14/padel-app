import { TypeMatch, StatutMatch } from './enums.model';

// Correspond à MatchResponse.java — enrichi avec les infos organisateur/terrain/site
export interface MatchResponse {
  id: number;
  terrainId: number;
  terrainNumero: number;
  siteId: number;
  nomSite: string;
  organisateurId: number;
  nomOrganisateur: string;
  prenomOrganisateur: string;
  dateHeure: string; // LocalDateTime → string ISO
  type: TypeMatch;
  statut: StatutMatch;
  montantTotal: number;
  devenuPublicLe: string | null;
}

// Correspond à MatchCreateRequest.java
export interface MatchCreateRequest {
  terrainId: number;
  organisateurId: number;
  dateHeure: string;
  type: TypeMatch;
}