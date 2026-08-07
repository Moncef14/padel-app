package be.ephec.padel.repositories;
import be.ephec.padel.models.JourFermeture;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JourFermetureRepository extends JpaRepository<JourFermeture, Long> {

    List<JourFermeture> findBySiteId(Long siteId);

    // fermetures globales (jours feries) applicables a tous les sites
    List<JourFermeture> findBySiteIsNull();

    List<JourFermeture> findByDate(LocalDate date);

    // MatchService.reserver : le site est ferme si une fermeture globale existe pour cette date
    boolean existsBySiteIsNullAndDate(LocalDate date);

    // MatchService.reserver : le site est ferme si une fermeture propre a ce site existe pour cette date
    boolean existsBySiteIdAndDate(Long siteId, LocalDate date);
}