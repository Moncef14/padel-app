package be.ephec.padel.repositories;
import be.ephec.padel.models.Site;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {

    // sites de la saison en cours uniquement ; les sites clotures restent en base pour l'historique des matchs
    List<Site> findByActifTrue();
}