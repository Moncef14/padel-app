package be.ephec.padel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private long totalMatchs;
    private long totalMembres;
    private BigDecimal chiffreAffaires;
    private double tauxOccupation;
    private long matchsEnAttente;
    private long matchsComplets;
    private long matchsAnnules;
}
