export interface JourFermeture {
  id: number;
  date: string; // LocalDate → string ISO (YYYY-MM-DD)
  siteId: number | null; // null = fermeture globale
}