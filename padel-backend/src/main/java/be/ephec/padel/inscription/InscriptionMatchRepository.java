package be.ephec.padel.inscription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscriptionMatchRepository extends JpaRepository<InscriptionMatch, Long> {

    List<InscriptionMatch> findByMatchId(Long matchId);

    List<InscriptionMatch> findByMembreId(Long membreId);

    List<InscriptionMatch> findByMatchIdAndStatutPaiement(Long matchId, StatutPaiement statutPaiement);
}