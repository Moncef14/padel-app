package be.ephec.padel.dto;

import be.ephec.padel.models.enums.TypeMembre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembreCreateRequest {
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private TypeMembre type;
    private Long siteId;
    private BigDecimal soldeDu;
}