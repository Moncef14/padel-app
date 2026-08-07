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

    // SchedulerService (job de minuit) : places encore INSCRIT (non payees) sur les matchs du lendemain, a liberer automatiquement
    List<InscriptionMatch> findByStatutPaiementAndMatch_DateHeureBetween(StatutPaiement statut, LocalDateTime debut, LocalDateTime fin);

    // nombre de places payees sur un match : sert a determiner s'il est COMPLET (4) ou doit basculer en DEVENU_PUBLIC
    int countByMatchIdAndStatutPaiement(Long matchId, StatutPaiement statut);

    // verifie la contrainte d'unicite (match, membre) avant inscription, pour un message d'erreur metier plutot qu'une violation SQL
    boolean existsByMatchIdAndMembreId(Long matchId, Long membreId);

    // Chiffre d'affaires : somme des places payées sur un ensemble de matchs
    @Query("SELECT COALESCE(SUM(i.montantPaye), 0) FROM InscriptionMatch i " +
            "WHERE i.match.id IN :matchIds AND i.statutPaiement = be.ephec.padel.models.enums.StatutPaiement.PAYE")
    BigDecimal sumMontantPayeByMatchIds(@Param("matchIds") List<Long> matchIds);
}