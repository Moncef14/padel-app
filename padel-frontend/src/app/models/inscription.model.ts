import { StatutPaiement } from './enums.model';

export interface InscriptionMatchResponse {
  id: number;
  matchId: number;
  membreId: number;
  nomMembre: string;
  prenomMembre: string;
  matriculeMembre: string;
  statutPaiement: StatutPaiement;
  montantPaye: number;
  datePaiement: string | null;
}