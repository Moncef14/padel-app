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

    @Scheduled(cron = "0 59 23 * * *")
    @Transactional
    public void basculerMatchsPrives() {
        LocalDateTime debutDemain = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime finDemain = LocalDate.now().plusDays(1).atTime(23, 59, 59);

        // ÉTAPE A — LIBÉRER LES PLACES NON PAYÉES
        List<InscriptionMatch> inscriptionsInscrit = inscriptionMatchRepository
                .findByStatutPaiementAndMatch_DateHeureBetween(StatutPaiement.INSCRIT, debutDemain, finDemain);
        for (InscriptionMatch inscription : inscriptionsInscrit) {
            inscription.setStatutPaiement(StatutPaiement.ANNULE);
            inscriptionMatchRepository.save(inscription);
            log.info("Place annulée pour le membre {} au match {}",
                    inscription.getMembre().getId(), inscription.getMatch().getId());
        }

        // ÉTAPE B — BASCULER LES MATCHS PRIVÉS EN_ATTENTE
        List<Match> matchsPrive = matchRepository
                .findByTypeAndStatutAndDateHeureBetween(TypeMatch.PRIVE, StatutMatch.EN_ATTENTE, debutDemain, finDemain);
        for (Match match : matchsPrive) {
            int nbPayes = inscriptionMatchRepository.countByMatchIdAndStatutPaiement(match.getId(), StatutPaiement.PAYE);
            if (nbPayes < 4) {
                match.setStatut(StatutMatch.DEVENU_PUBLIC);
                match.setDevenuPublicLe(LocalDate.now().atStartOfDay());
                Membre organisateur = membreRepository.findById(match.getOrganisateur().getId())
                        .orElseThrow(() -> new RuntimeException("Organisateur non trouvé : " + match.getOrganisateur().getId()));
                organisateur.setPenaliteJusquAu(LocalDate.now().plusDays(7));
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
        List<Match> matchsDevenuPublic = matchRepository
                .findByStatutAndDateHeureBetween(StatutMatch.DEVENU_PUBLIC, debutDemain, finDemain);
        for (Match match : matchsDevenuPublic) {
            int nbPayes = inscriptionMatchRepository.countByMatchIdAndStatutPaiement(match.getId(), StatutPaiement.PAYE);
            Membre organisateur = membreRepository.findById(match.getOrganisateur().getId())
                    .orElseThrow(() -> new RuntimeException("Organisateur non trouvé : " + match.getOrganisateur().getId()));
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