package be.ephec.padel.services;
import be.ephec.padel.models.Site;
import be.ephec.padel.repositories.SiteRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteService {

    private final SiteRepository siteRepository;

    public SiteService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public List<Site> getAll() {
        return siteRepository.findAll();
    }

    public Site getById(Long id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site non trouvé avec l'id : " + id));
    }

    public List<Site> getActifs() {
        return siteRepository.findByActifTrue();
    }

    public Site create(Site site) {
        return siteRepository.save(site);
    }

    public Site update(Long id, Site site) {
        Site existing = getById(id);
        existing.setNom(site.getNom());
        existing.setHeureOuverture(site.getHeureOuverture());
        existing.setHeureFermeture(site.getHeureFermeture());
        existing.setActif(site.isActif());
        return siteRepository.save(existing);
    }

    public void delete(Long id) {
        siteRepository.deleteById(id);
    }
}
