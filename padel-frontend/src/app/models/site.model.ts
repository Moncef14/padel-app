export interface Site {
  id: number;
  nom: string;
  heureOuverture: string; // format HH:mm:ss côté backend
  heureFermeture: string;
  actif: boolean;
  annee: number;
}