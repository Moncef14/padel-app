package be.ephec.padel.dto;

import be.ephec.padel.models.enums.StatutMatch;
import be.ephec.padel.models.enums.TypeMatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResponse {
    private Long id;
    private Long terrainId;
    private Integer terrainNumero;
    private Long siteId;
    private String nomSite;
    private Long organisateurId;
    private String nomOrganisateur;
    private String prenomOrganisateur;
    private LocalDateTime dateHeure;
    private TypeMatch type;
    private StatutMatch statut;
    private BigDecimal montantTotal;
    private LocalDateTime devenuPublicLe;
}