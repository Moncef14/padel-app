package be.ephec.padel.repositories;
import be.ephec.padel.models.InscriptionMatch;
import be.ephec.padel.models.enums.StatutPaiement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscriptionMatchRepository extends JpaRepository<InscriptionMatch, Long> {

    List<InscriptionMatch> findByMatchId(Long matchId);

    List<InscriptionMatch> findByMembreId(Long membreId);

    List<InscriptionMatch> findByMatchIdAndStatutPaiement(Long matchId, StatutPaiement statutPaiement);
}