package be.ephec.padel.services;

import be.ephec.padel.models.InscriptionMatch;
import be.ephec.padel.models.Match;
import be.ephec.padel.models.Membre;
import be.ephec.padel.models.enums.StatutMatch;
import be.ephec.padel.models.enums.StatutPaiement;
import be.ephec.padel.models.enums.TypeMatch;
import be.ephec.padel.repositories.InscriptionMatchRepository;
import be.ephec.padel.repositories.MatchRepository;
import be.ephec.padel.repositories.MembreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SchedulerService {

    private final MatchRepository matchRepository;
    private final InscriptionMatchRepository inscriptionMatchRepository;
    private final MembreRepository membreRepository;

    public SchedulerService(MatchRepository matchRepository,
                            InscriptionMatchRepository inscriptionMatchRepository,
                            MembreRepository membreRepository) {
        this.matchRepository = matchRepository;
        this.inscriptionMatchRepository = inscriptionMatchRepository;
        this.membreRepository = membreRepository;
    }

    // tourne chaque nuit à 23h59 sur les matchs du lendemain : dernier moment pour régulariser avant le jour J
    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void basculerMatchsPrives() {
        LocalDateTime debutDemain = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime finDemain = LocalDate.now().plusDays(1).atTime(23, 59, 59);

        // ÉTAPE A — LIBÉRER LES PLACES NON PAYÉES
        // une place INSCRIT jamais payée jusqu'à la veille est considérée abandonnée ; elle ne compte plus dans le nombre de PAYE de l'étape B
        List<InscriptionMatch> inscriptionsInscrit = inscriptionMatchRepository
                .findByStatutPaiementAndMatch_DateHeureBetween(StatutPaiement.INSCRIT, debutDemain, finDemain);
        for (InscriptionMatch inscription : inscriptionsInscrit) {
            inscription.setStatutPaiement(StatutPaiement.ANNULE);
            inscriptionMatchRepository.save(inscription);
            log.info("Place annulée pour le membre {} au match {}",
                    inscription.getMembre().getId(), inscription.getMatch().getId());
        }

        // ÉTAPE B — BASCULER LES MATCHS PRIVÉS EN_ATTENTE
        // un match PRIVE qui n'a pas trouvé ses 4 joueurs payants la veille devient public : les places vides sont ouvertes à tous
        List<Match> matchsPrive = matchRepository
                .findByTypeAndStatutAndDateHeureBetween(TypeMatch.PRIVE, StatutMatch.EN_ATTENTE, debutDemain, finDemain);
        for (Match match : matchsPrive) {
            int nbPayes = inscriptionMatchRepository.countByMatchIdAndStatutPaiement(match.getId(), StatutPaiement.PAYE);
            if (nbPayes < 4) {
                match.setStatut(StatutMatch.DEVENU_PUBLIC);
                match.setDevenuPublicLe(LocalDate.now().atStartOfDay());
                Membre organisateur = membreRepository.findById(match.getOrganisateur().getId())
                        .orElseThrow(() -> new RuntimeException("Organisateur non trouvé : " + match.getOrganisateur().getId()));
                // pénalité de 7 jours : sanctionne l'organisateur pour ne pas avoir complété son match, bloque toute nouvelle réservation (cf. MatchService.create)
                organisateur.setPenaliteJusquAu(LocalDate.now().plusDays(7));
                // l'organisateur assume le coût des places vides (15€/place) qu'il n'a pas su remplir, à régler avant sa prochaine réservation
                int placesVides = 4 - nbPayes;
                organisateur.setSoldeDu(organisateur.getSoldeDu()
                        .add(BigDecimal.valueOf(placesVides).multiply(new BigDecimal("15"))));
                matchRepository.save(match);
                membreRepository.save(organisateur);
                log.info("Match {} basculé en DEVENU_PUBLIC, pénalité appliquée à {}",
                        match.getId(), organisateur.getId());
            }
        }

        // ÉTAPE C — VÉRIFIER LES MATCHS DÉJÀ DEVENU_PUBLIC
        // un match déjà public la veille (converti un jour antérieur) peut encore se remplir via inscrireEtPayer ; on ne repénalise que si toujours incomplet
        List<Match> matchsDevenuPublic = matchRepository
                .findByStatutAndDateHeureBetween(StatutMatch.DEVENU_PUBLIC, debutDemain, finDemain);
        for (Match match : matchsDevenuPublic) {
            int nbPayes = inscriptionMatchRepository.countByMatchIdAndStatutPaiement(match.getId(), StatutPaiement.PAYE);
            Membre organisateur = membreRepository.findById(match.getOrganisateur().getId())
                    .orElseThrow(() -> new RuntimeException("Organisateur non trouvé : " + match.getOrganisateur().getId()));
            // évite de réappliquer/prolonger une pénalité déjà active pour ce même match (l'étape B l'a déjà posée le jour de la bascule)
            if (nbPayes < 4
                    && (organisateur.getPenaliteJusquAu() == null
                        || organisateur.getPenaliteJusquAu().isBefore(LocalDate.now()))) {
                int placesVides = 4 - nbPayes;
                organisateur.setPenaliteJusquAu(LocalDate.now().plusDays(7));
                organisateur.setSoldeDu(organisateur.getSoldeDu()
                        .add(BigDecimal.valueOf(placesVides).multiply(new BigDecimal("15"))));
                membreRepository.save(organisateur);
                log.info("Solde dû mis à jour pour l'organisateur {} du match DEVENU_PUBLIC {}",
                        organisateur.getId(), match.getId());
            }
        }
    }
}