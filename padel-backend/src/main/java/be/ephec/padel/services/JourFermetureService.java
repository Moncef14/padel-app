package be.ephec.padel.services;
import be.ephec.padel.models.JourFermeture;
import be.ephec.padel.repositories.JourFermetureRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JourFermetureService {

    private final JourFermetureRepository jourFermetureRepository;

    public JourFermetureService(JourFermetureRepository jourFermetureRepository) {
        this.jourFermetureRepository = jourFermetureRepository;
    }

    public List<JourFermeture> getAll() {
        return jourFermetureRepository.findAll();
    }

    public JourFermeture getById(Long id) {
        return jourFermetureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jour de fermeture non trouvé avec l'id : " + id));
    }

    public List<JourFermeture> getBySiteId(Long siteId) {
        return jourFermetureRepository.findBySiteId(siteId);
    }

    public List<JourFermeture> getGlobaux() {
        return jourFermetureRepository.findBySiteIsNull();
    }

    public JourFermeture create(JourFermeture jourFermeture) {
        return jourFermetureRepository.save(jourFermeture);
    }

    public void delete(Long id) {
        jourFermetureRepository.deleteById(id);
    }
}