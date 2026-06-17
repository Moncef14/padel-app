package be.ephec.padel.services;
import be.ephec.padel.models.Membre;
import be.ephec.padel.repositories.MembreRepository;
import be.ephec.padel.models.enums.TypeMembre;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembreService {

    private final MembreRepository membreRepository;

    public MembreService(MembreRepository membreRepository) {
        this.membreRepository = membreRepository;
    }

    public List<Membre> getAll() {
        return membreRepository.findAll();
    }

    public Membre getById(Long id) {
        return membreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membre non trouvé avec l'id : " + id));
    }

    public List<Membre> getByType(TypeMembre type) {
        return membreRepository.findByType(type);
    }

    public List<Membre> getBySiteId(Long siteId) {
        return membreRepository.findBySiteId(siteId);
    }

    public Membre create(Membre membre) {
        return membreRepository.save(membre);
    }

    public Membre update(Long id, Membre membre) {
        Membre existing = getById(id);
        existing.setMatricule(membre.getMatricule());
        existing.setNom(membre.getNom());
        existing.setPrenom(membre.getPrenom());
        existing.setEmail(membre.getEmail());
        existing.setType(membre.getType());
        existing.setSite(membre.getSite());
        existing.setSoldeDu(membre.getSoldeDu());
        existing.setPenaliteJusquAu(membre.getPenaliteJusquAu());
        return membreRepository.save(existing);
    }

    public void delete(Long id) {
        membreRepository.deleteById(id);
    }
}