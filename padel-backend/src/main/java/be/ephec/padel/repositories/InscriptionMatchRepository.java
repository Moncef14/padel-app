package be.ephec.padel.repositories;
import be.ephec.padel.models.InscriptionMatch;
import be.ephec.padel.models.enums.StatutPaiement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InscriptionMatchRepository extends JpaRepository<InscriptionMatch, Long> {

    List<InscriptionMatch> findByMatchId(Long matchId);

    List<InscriptionMatch> findByMembreId(Long membreId);

    List<InscriptionMatch> findByMatchIdAndStatutPaiement(Long matchId, StatutPaiement statutPaiement);

    List<InscriptionMatch> findByStatutPaiementAndMatch_DateHeureBetween(StatutPaiement statut, LocalDateTime debut, LocalDateTime fin);

    int countByMatchIdAndStatutPaiement(Long matchId, StatutPaiement statut);
}