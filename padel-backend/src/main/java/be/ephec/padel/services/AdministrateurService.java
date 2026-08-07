package be.ephec.padel.services;
import be.ephec.padel.dto.AdministrateurCreateRequest;
import be.ephec.padel.dto.AdministrateurResponse;
import be.ephec.padel.models.Administrateur;
import be.ephec.padel.models.Site;
import be.ephec.padel.repositories.AdministrateurRepository;
import be.ephec.padel.models.enums.RoleAdmin;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdministrateurService {

    private final AdministrateurRepository administrateurRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdministrateurService(AdministrateurRepository administrateurRepository, BCryptPasswordEncoder passwordEncoder) {
        this.administrateurRepository = administrateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Administrateur> getAll() {
        return administrateurRepository.findAll();
    }

    public Administrateur getById(Long id) {
        return administrateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé avec l'id : " + id));
    }

    public Administrateur getByEmail(String email) {
        return administrateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé avec l'email : " + email));
    }

    public List<Administrateur> getByRole(RoleAdmin role) {
        return administrateurRepository.findByRole(role);
    }

    // mot de passe jamais stocké en clair : hashé avant persistance, comme pour Membre.inscrire
    public Administrateur create(Administrateur administrateur) {
        administrateur.setMotDePasse(passwordEncoder.encode(administrateur.getMotDePasse()));
        return administrateurRepository.save(administrateur);
    }

    public Administrateur update(Long id, Administrateur administrateur) {
        Administrateur existing = getById(id);
        existing.setNom(administrateur.getNom());
        existing.setEmail(administrateur.getEmail());
        // ne re-hash que si un nouveau mot de passe est fourni, sinon un update sans mot de passe écraserait le hash existant par un champ vide
        if (administrateur.getMotDePasse() != null && !administrateur.getMotDePasse().isBlank()) {
            existing.setMotDePasse(passwordEncoder.encode(administrateur.getMotDePasse()));
        }
        existing.setRole(administrateur.getRole());
        existing.setSite(administrateur.getSite());
        return administrateurRepository.save(existing);
    }

    public void delete(Long id) {
        Administrateur admin = getById(id);

        // évite de rendre le système inadministrable : il doit toujours rester au moins un ADMIN_GLOBAL pour gérer tous les sites
        if (admin.getRole() == RoleAdmin.ADMIN_GLOBAL) {
            long nombreAdminsGlobaux = administrateurRepository.findByRole(RoleAdmin.ADMIN_GLOBAL).size();
            if (nombreAdminsGlobaux <= 1) {
                throw new RuntimeException("Impossible de supprimer le dernier administrateur global du système");
            }
        }

        administrateurRepository.deleteById(id);
    }

    public AdministrateurResponse toResponse(Administrateur a) {
        return AdministrateurResponse.builder()
                .id(a.getId())
                .nom(a.getNom())
                .prenom(a.getPrenom())
                .email(a.getEmail())
                .role(a.getRole())
                .siteId(a.getSite() != null ? a.getSite().getId() : null)
                .nomSite(a.getSite() != null ? a.getSite().getNom() : null)
                .build();
    }

    public Administrateur toEntity(AdministrateurCreateRequest dto) {
        Administrateur admin = Administrateur.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .motDePasse(dto.getMotDePasse())
                .role(dto.getRole())
                .build();
        if (dto.getSiteId() != null) {
            Site site = new Site();
            site.setId(dto.getSiteId());
            admin.setSite(site);
        }
        return admin;
    }

    public List<AdministrateurResponse> getAllAsResponse() {
        return administrateurRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AdministrateurResponse getByIdAsResponse(Long id) {
        return toResponse(getById(id));
    }

    public AdministrateurResponse createFromRequest(AdministrateurCreateRequest dto) {
        return toResponse(create(toEntity(dto)));
    }
}