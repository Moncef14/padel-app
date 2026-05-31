package be.ephec.padel.fermeture;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JourFermetureRepository extends JpaRepository<JourFermeture, Long> {

    List<JourFermeture> findBySiteId(Long siteId);

    List<JourFermeture> findBySiteIsNull();

    List<JourFermeture> findByDate(LocalDate date);
}