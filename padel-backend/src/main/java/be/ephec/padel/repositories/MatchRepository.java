package be.ephec.padel.repositories;
import be.ephec.padel.models.Match;
import be.ephec.padel.models.enums.StatutMatch;
import be.ephec.padel.models.enums.TypeMatch;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByStatut(StatutMatch statut);

    List<Match> findByType(TypeMatch type);

    List<Match> findByOrganisateurId(Long organisateurId);
}