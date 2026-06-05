package be.ephec.padel.match;

import org.springframework.stereotype.Service;

import java.util.List;

import java.math.BigDecimal;

import java.time.LocalDate;

import java.time.LocalDateTime;

import be.ephec.padel.membre.MembreRepository;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final MembreRepository membreRepository;

    public MatchService(MatchRepository matchRepository, MembreRepository membreRepository) {
        this.matchRepository = matchRepository;
        this.membreRepository = membreRepository;
    }

    public List<Match> getAll() {
        return matchRepository.findAll();
    }

    public Match getById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match non trouvé avec l'id : " + id));
    }

    public List<Match> getByStatut(StatutMatch statut) {
        return matchRepository.findByStatut(statut);
    }

    public List<Match> getByType(TypeMatch type) {
        return matchRepository.findByType(type);
    }

    public List<Match> getPublics() {
        return matchRepository.findByType(TypeMatch.PUBLIC);
    }

    public Match create(Match match) { // Modife faite le 05/06/26 après la remise
        Long organisateurId = match.getOrganisateur().getId();
        var organisateur = membreRepository.findById(organisateurId)
            .orElseThrow(() -> new RuntimeException("Membre non trouvé avec l'id : " + organisateurId));
        match.setOrganisateur(organisateur);
        var dateHeure = match.getDateHeure();

        if (dateHeure == null || dateHeure.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Réservation impossible : la date doit être dans le futur");
        }

        if (organisateur.getSoldeDu().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Réservation impossible : solde dû de " + organisateur.getSoldeDu() + "€");
        }

        if (organisateur.getPenaliteJusquAu() != null &&
                organisateur.getPenaliteJusquAu().isAfter(LocalDate.now())) {
            throw new RuntimeException("Réservation impossible : pénalité active jusqu'au " + organisateur.getPenaliteJusquAu());
        }

        long joursAvant = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), dateHeure.toLocalDate());
        switch (organisateur.getType()) {
            case GLOBAL -> {
                if (joursAvant < 21)
                    throw new RuntimeException("Réservation impossible : délai insuffisant pour un membre GLOBAL (21 jours minimum)");
            }
            case SITE -> {
                if (joursAvant < 14)
                    throw new RuntimeException("Réservation impossible : délai insuffisant pour un membre SITE (14 jours minimum)");
            }
            case LIBRE -> {
                if (joursAvant < 5)
                    throw new RuntimeException("Réservation impossible : délai insuffisant pour un membre LIBRE (5 jours minimum)");
            }
        }

        return matchRepository.save(match);
    }

    public Match update(Long id, Match match) {
        Match existing = getById(id);
        existing.setTerrain(match.getTerrain());
        existing.setDateHeure(match.getDateHeure());
        existing.setType(match.getType());
        existing.setOrganisateur(match.getOrganisateur());
        existing.setStatut(match.getStatut());
        existing.setMontantTotal(match.getMontantTotal());
        existing.setDevenuPublicLe(match.getDevenuPublicLe());
        return matchRepository.save(existing);
    }

    public void delete(Long id) {
        matchRepository.deleteById(id);
    }
}