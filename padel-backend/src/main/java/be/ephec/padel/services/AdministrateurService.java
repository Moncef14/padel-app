package be.ephec.padel.services;
import be.ephec.padel.models.Administrateur;
import be.ephec.padel.repositories.AdministrateurRepository;
import be.ephec.padel.models.enums.RoleAdmin;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Administrateur create(Administrateur administrateur) {
        administrateur.setMotDePasse(passwordEncoder.encode(administrateur.getMotDePasse()));
        return administrateurRepository.save(administrateur);
    }

    public Administrateur update(Long id, Administrateur administrateur) {
        Administrateur existing = getById(id);
        existing.setNom(administrateur.getNom());
        existing.setEmail(administrateur.getEmail());
        if (administrateur.getMotDePasse() != null && !administrateur.getMotDePasse().isBlank()) {
            existing.setMotDePasse(passwordEncoder.encode(administrateur.getMotDePasse()));
        }
        existing.setRole(administrateur.getRole());
        existing.setSite(administrateur.getSite());
        return administrateurRepository.save(existing);
    }

    public void delete(Long id) {
        administrateurRepository.deleteById(id);
    }
}