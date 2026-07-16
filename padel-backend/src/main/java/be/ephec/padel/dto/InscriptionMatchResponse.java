package be.ephec.padel.dto;

import be.ephec.padel.models.enums.StatutPaiement;
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
public class InscriptionMatchResponse {
    private Long id;
    private Long matchId;
    private Long membreId;
    private String nomMembre;
    private String prenomMembre;
    private String matriculeMembre;
    private StatutPaiement statutPaiement;
    private BigDecimal montantPaye;
    private LocalDateTime datePaiement;
}