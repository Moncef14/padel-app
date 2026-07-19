package be.ephec.padel.repositories;
import be.ephec.padel.models.InscriptionMatch;
import be.ephec.padel.models.enums.StatutPaiement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface InscriptionMatchRepository extends JpaRepository<InscriptionMatch, Long> {

    List<InscriptionMatch> findByMatchId(Long matchId);

    List<InscriptionMatch> findByMembreId(Long membreId);

    List<InscriptionMatch> findByMatchIdAndStatutPaiement(Long matchId, StatutPaiement statutPaiement);

    List<InscriptionMatch> findByStatutPaiementAndMatch_DateHeureBetween(StatutPaiement statut, LocalDateTime debut, LocalDateTime fin);

    int countByMatchIdAndStatutPaiement(Long matchId, StatutPaiement statut);

    boolean existsByMatchIdAndMembreId(Long matchId, Long membreId);

    // Chiffre d'affaires : somme des places payées sur un ensemble de matchs
    @Query("SELECT COALESCE(SUM(i.montantPaye), 0) FROM InscriptionMatch i " +
            "WHERE i.match.id IN :matchIds AND i.statutPaiement = be.ephec.padel.models.enums.StatutPaiement.PAYE")
    BigDecimal sumMontantPayeByMatchIds(@Param("matchIds") List<Long> matchIds);
}