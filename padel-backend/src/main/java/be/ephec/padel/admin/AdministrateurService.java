package be.ephec.padel.admin;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministrateurService {

    private final AdministrateurRepository administrateurRepository;

    public AdministrateurService(AdministrateurRepository administrateurRepository) {
        this.administrateurRepository = administrateurRepository;
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
        return administrateurRepository.save(administrateur);
    }

    public Administrateur update(Long id, Administrateur administrateur) {
        Administrateur existing = getById(id);
        existing.setNom(administrateur.getNom());
        existing.setEmail(administrateur.getEmail());
        existing.setMotDePasse(administrateur.getMotDePasse());
        existing.setRole(administrateur.getRole());
        existing.setSite(administrateur.getSite());
        return administrateurRepository.save(existing);
    }

    public void delete(Long id) {
        administrateurRepository.deleteById(id);
    }
}