package be.ephec.padel.services;
import be.ephec.padel.dto.DashboardStatsResponse;
import be.ephec.padel.models.Match;
import be.ephec.padel.models.enums.StatutMatch;
import be.ephec.padel.repositories.InscriptionMatchRepository;
import be.ephec.padel.repositories.MatchRepository;
import be.ephec.padel.repositories.MembreRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final MatchRepository matchRepository;
    private final MembreRepository membreRepository;
    private final InscriptionMatchRepository inscriptionMatchRepository;

    public StatsService(MatchRepository matchRepository, MembreRepository membreRepository, InscriptionMatchRepository inscriptionMatchRepository) {
        this.matchRepository = matchRepository;
        this.membreRepository = membreRepository;
        this.inscriptionMatchRepository = inscriptionMatchRepository;
    }

    public DashboardStatsResponse getStats(Long siteId) {
        // Si siteId null → stats globales (ADMIN_GLOBAL)
        // Si siteId fourni → stats filtrées sur le site (ADMIN_SITE)
        List<Match> matchs = siteId != null
                ? matchRepository.findByTerrain_SiteId(siteId)
                : matchRepository.findAll();

        long total = matchs.size();
        long complets = matchs.stream()
                .filter(m -> m.getStatut() == StatutMatch.COMPLET).count();
        long enAttente = matchs.stream()
                .filter(m -> m.getStatut() == StatutMatch.EN_ATTENTE).count();
        long annules = matchs.stream()
                .filter(m -> m.getStatut() == StatutMatch.ANNULE).count();

        double tauxOccupation = total > 0 ? (complets * 100.0) / total : 0.0;

        long totalMembres = siteId != null
                ? membreRepository.findBySiteId(siteId).size()
                : membreRepository.count();

        // Chiffre d'affaires = somme des places payées sur les matchs du périmètre
        List<Long> matchIds = matchs.stream().map(Match::getId).collect(Collectors.toList());
        BigDecimal chiffreAffaires = matchIds.isEmpty()
                ? BigDecimal.ZERO
                : inscriptionMatchRepository.sumMontantPayeByMatchIds(matchIds);

        return DashboardStatsResponse.builder()
                .totalMatchs(total)
                .totalMembres(totalMembres)
                .chiffreAffaires(chiffreAffaires)
                .tauxOccupation(tauxOccupation)
                .matchsEnAttente(enAttente)
                .matchsComplets(complets)
                .matchsAnnules(annules)
                .build();
    }
}
