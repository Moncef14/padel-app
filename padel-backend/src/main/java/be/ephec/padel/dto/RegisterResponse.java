package be.ephec.padel.dto;

import be.ephec.padel.models.enums.TypeMembre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private Long id;
    private String token;
    private String matricule;
    private String nom;
    private String prenom;
    private TypeMembre type;
    private Long siteId;
}