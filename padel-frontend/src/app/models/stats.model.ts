// Correspond à DashboardStatsResponse.java
export interface DashboardStats {
  totalMatchs: number;
  totalMembres: number;
  chiffreAffaires: number;
  tauxOccupation: number;
  matchsEnAttente: number;
  matchsComplets: number;
  matchsAnnules: number;
}

export interface StatsParSite {
  siteId: number;
  nomSite: string;
  totalMatchs: number;
  chiffreAffaires: number;
  tauxOccupation: number;
}