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
public class StatsParSiteResponse {
    private Long siteId;
    private String nomSite;
    private long totalMatchs;
    private BigDecimal chiffreAffaires;
    private double tauxOccupation;
}