package be.ephec.padel.dto;

import be.ephec.padel.models.enums.RoleAdmin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministrateurResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private RoleAdmin role;
    private Long siteId;
    private String nomSite;
}