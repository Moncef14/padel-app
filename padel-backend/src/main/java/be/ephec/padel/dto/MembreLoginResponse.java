package be.ephec.padel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MembreLoginResponse {
    private String token;
    private String matricule;
    private String type;
    private String nom;
    private String prenom;
}