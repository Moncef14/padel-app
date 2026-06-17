package be.ephec.padel.services;
import be.ephec.padel.models.InscriptionMatch;
import be.ephec.padel.repositories.InscriptionMatchRepository;
import be.ephec.padel.models.enums.StatutPaiement;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InscriptionMatchService {

    private final InscriptionMatchRepository inscriptionMatchRepository;

    public InscriptionMatchService(InscriptionMatchRepository inscriptionMatchRepository) {
        this.inscriptionMatchRepository = inscriptionMatchRepository;
    }

    public List<InscriptionMatch> getAll() {
        return inscriptionMatchRepository.findAll();
    }

    public InscriptionMatch getById(Long id) {
        return inscriptionMatchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée avec l'id : " + id));
    }

    public List<InscriptionMatch> getByMatchId(Long matchId) {
        return inscriptionMatchRepository.findByMatchId(matchId);
    }

    public List<InscriptionMatch> getByMembreId(Long membreId) {
        return inscriptionMatchRepository.findByMembreId(membreId);
    }

    public List<InscriptionMatch> getJoueursPayes(Long matchId) {
        return inscriptionMatchRepository.findByMatchIdAndStatutPaiement(matchId, StatutPaiement.PAYE);
    }

    public InscriptionMatch create(InscriptionMatch inscription) {
        return inscriptionMatchRepository.save(inscription);
    }

    public InscriptionMatch update(Long id, InscriptionMatch inscription) {
        InscriptionMatch existing = getById(id);
        existing.setMatch(inscription.getMatch());
        existing.setMembre(inscription.getMembre());
        existing.setStatutPaiement(inscription.getStatutPaiement());
        existing.setMontantPaye(inscription.getMontantPaye());
        existing.setDatePaiement(inscription.getDatePaiement());
        return inscriptionMatchRepository.save(existing);
    }

    public void delete(Long id) {
        inscriptionMatchRepository.deleteById(id);
    }
}