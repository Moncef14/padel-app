package be.ephec.padel.repositories;
import be.ephec.padel.models.Match;
import be.ephec.padel.models.enums.StatutMatch;
import be.ephec.padel.models.enums.TypeMatch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByStatut(StatutMatch statut);

    List<Match> findByType(TypeMatch type);

    List<Match> findByOrganisateurId(Long organisateurId);

    // SchedulerService (job de minuit) : matchs prives EN_ATTENTE du lendemain, candidats a la bascule DEVENU_PUBLIC si pas complets
    List<Match> findByTypeAndStatutAndDateHeureBetween(TypeMatch type, StatutMatch statut, LocalDateTime debut, LocalDateTime fin);

    // SchedulerService : matchs deja DEVENU_PUBLIC du lendemain, pour re-verifier si l'organisateur doit encore etre penalise
    List<Match> findByStatutAndDateHeureBetween(StatutMatch statut, LocalDateTime debut, LocalDateTime fin);

    boolean existsByTerrainIdAndDateHeureBetween(Long terrainId, LocalDateTime debut, LocalDateTime fin);

    // MatchService.reserver : verifie qu'aucun match actif n'occupe deja le terrain sur le creneau + battement (1h45), en ignorant les matchs ANNULE
    boolean existsByTerrainIdAndDateHeureBetweenAndStatutNot(Long terrainId, LocalDateTime debut, LocalDateTime fin, StatutMatch statut);

    List<Match> findByTerrain_SiteId(Long siteId);

    // historique d'un membre : matchs qu'il a organises OU auxquels il est inscrit avec une place non annulee
    @Query("SELECT DISTINCT m FROM Match m " +
           "LEFT JOIN InscriptionMatch i ON i.match = m " +
           "WHERE m.organisateur.id = :membreId " +
           "OR (i.membre.id = :membreId AND i.statutPaiement != be.ephec.padel.models.enums.StatutPaiement.ANNULE) " +
           "ORDER BY m.dateHeure DESC")
    List<Match> findByMembreParticipant(@Param("membreId") Long membreId);
}