package be.ephec.padel.services;
import be.ephec.padel.models.Match;
import be.ephec.padel.repositories.MatchRepository;
import be.ephec.padel.models.enums.StatutMatch;
import be.ephec.padel.models.enums.TypeMatch;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.math.BigDecimal;

import java.time.LocalDate;

import java.time.LocalDateTime;

import be.ephec.padel.models.InscriptionMatch;
import be.ephec.padel.models.enums.StatutPaiement;
import be.ephec.padel.repositories.InscriptionMatchRepository;
import be.ephec.padel.repositories.JourFermetureRepository;
import be.ephec.padel.repositories.MembreRepository;
import be.ephec.padel.repositories.TerrainRepository;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final MembreRepository membreRepository;
    private final TerrainRepository terrainRepository;
    private final JourFermetureRepository jourFermetureRepository;
    private final InscriptionMatchRepository inscriptionMatchRepository;

    public MatchService(MatchRepository matchRepository, MembreRepository membreRepository, TerrainRepository terrainRepository, JourFermetureRepository jourFermetureRepository, InscriptionMatchRepository inscriptionMatchRepository) {
        this.matchRepository = matchRepository;
        this.membreRepository = membreRepository;
        this.terrainRepository = terrainRepository;
        this.jourFermetureRepository = jourFermetureRepository;
        this.inscriptionMatchRepository = inscriptionMatchRepository;
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

    public List<Match> getBySiteId(Long siteId) {
        return matchRepository.findByTerrain_SiteId(siteId);
    }

    @Transactional
    public Match create(Match match) { // Modifications post-remise : règles métier
        // Chargement complet de l'organisateur
        Long organisateurId = match.getOrganisateur().getId();
        var organisateur = membreRepository.findById(organisateurId)
                .orElseThrow(() -> new RuntimeException("Membre non trouvé avec l'id : " + organisateurId));
        match.setOrganisateur(organisateur);

        // Chargement complet du terrain
        Long terrainId = match.getTerrain().getId();
        var terrain = terrainRepository.findById(terrainId)
                .orElseThrow(() -> new RuntimeException("Terrain non trouvé avec l'id : " + terrainId));
        match.setTerrain(terrain);

        var dateHeure = match.getDateHeure();

        // Vérification date dans le futur
        if (dateHeure == null || dateHeure.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Réservation impossible : la date doit être dans le futur");
        }

        // Vérification solde dû
        if (organisateur.getSoldeDu().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Réservation impossible : solde dû de " + organisateur.getSoldeDu() + "€");
        }

        // Vérification pénalité active
        if (organisateur.getPenaliteJusquAu() != null &&
                organisateur.getPenaliteJusquAu().isAfter(LocalDate.now())) {
            throw new RuntimeException("Réservation impossible : pénalité active jusqu'au " + organisateur.getPenaliteJusquAu());
        }

        // Vérification délai selon type de membre
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

        // Vérification membre SITE dans son site uniquement
        if (organisateur.getType() == be.ephec.padel.models.enums.TypeMembre.SITE) {
            if (organisateur.getSite() == null ||
                    !organisateur.getSite().getId().equals(terrain.getSite().getId())) {
                throw new RuntimeException("Réservation impossible : un membre SITE ne peut réserver que dans son site ("
                        + (organisateur.getSite() != null ? organisateur.getSite().getNom() : "aucun site assigné") + ")");
            }
        }

        // Vérification jour de fermeture
        LocalDate dateMatch = dateHeure.toLocalDate();
        boolean jourFerme = jourFermetureRepository.existsBySiteIsNullAndDate(dateMatch) ||
            jourFermetureRepository.existsBySiteIdAndDate(terrain.getSite().getId(), dateMatch);
        if (jourFerme) {
            throw new RuntimeException("Réservation impossible : le site est fermé le " + dateMatch);
        }

        // VÉRIFICATION CRÉNEAU DISPONIBLE
        // Un match dure 1h30 + 15 min de battement = 1h45
        // On vérifie qu'aucun autre match n'occupe ce terrain
        // dans la fenêtre [dateHeure - 1h45, dateHeure + 1h45]
        // en excluant les matchs ANNULE
        LocalDateTime debutFenetre = dateHeure.minusMinutes(105);
        LocalDateTime finFenetre = dateHeure.plusMinutes(105);
        boolean creneauOccupe = matchRepository
                .existsByTerrainIdAndDateHeureBetweenAndStatutNot(
                        terrain.getId(), debutFenetre, finFenetre, StatutMatch.ANNULE);
        if (creneauOccupe)
            throw new RuntimeException(
                    "Créneau non disponible : ce terrain est déjà réservé sur ce créneau");

        // AUTO-INSCRIPTION DE L'ORGANISATEUR
        InscriptionMatch inscriptionOrganisateur = InscriptionMatch.builder()
                .match(match)
                .membre(organisateur)
                .statutPaiement(StatutPaiement.PAYE)
                .montantPaye(BigDecimal.valueOf(15).add(organisateur.getSoldeDu()))
                .datePaiement(LocalDateTime.now())
                .build();

        // Remettre le soldeDu à zéro
        organisateur.setSoldeDu(BigDecimal.ZERO);
        membreRepository.save(organisateur);

        // Sauvegarder le match d'abord pour avoir l'id
        Match savedMatch = matchRepository.save(match);

        // Puis sauvegarder l'inscription avec le match sauvegardé
        inscriptionOrganisateur.setMatch(savedMatch);
        inscriptionMatchRepository.save(inscriptionOrganisateur);

        return savedMatch;
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