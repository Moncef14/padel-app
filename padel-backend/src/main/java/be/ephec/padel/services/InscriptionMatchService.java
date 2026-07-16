package be.ephec.padel.services;
import be.ephec.padel.dto.InscriptionMatchResponse;
import be.ephec.padel.models.InscriptionMatch;
import be.ephec.padel.models.Match;
import be.ephec.padel.models.Membre;
import be.ephec.padel.repositories.InscriptionMatchRepository;
import be.ephec.padel.repositories.MatchRepository;
import be.ephec.padel.repositories.MembreRepository;
import be.ephec.padel.models.enums.StatutMatch;
import be.ephec.padel.models.enums.StatutPaiement;
import be.ephec.padel.models.enums.TypeMatch;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InscriptionMatchService {

    private final InscriptionMatchRepository inscriptionMatchRepository;
    private final MatchRepository matchRepository;
    private final MembreRepository membreRepository;

    public InscriptionMatchService(InscriptionMatchRepository inscriptionMatchRepository, MatchRepository matchRepository, MembreRepository membreRepository) {
        this.inscriptionMatchRepository = inscriptionMatchRepository;
        this.matchRepository = matchRepository;
        this.membreRepository = membreRepository;
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

    // Inscrire un joueur (match PRIVE uniquement, par l'organisateur)
    @Transactional
    public InscriptionMatch inscrireJoueur(Long matchId, Long membreId, Long organisateurId) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match non trouvé : " + matchId));

        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new RuntimeException("Membre non trouvé : " + membreId));

        // Vérifier que c'est un match PRIVE
        if (match.getType() != TypeMatch.PRIVE)
            throw new RuntimeException("Impossible : seul l'organisateur peut inscrire des joueurs sur un match privé");

        // Vérifier que c'est bien l'organisateur qui inscrit
        if (!match.getOrganisateur().getId().equals(organisateurId))
            throw new RuntimeException("Impossible : seul l'organisateur peut inscrire des joueurs");

        // Vérifier que le match n'est pas complet (max 4 PAYE)
        int nbPayes = inscriptionMatchRepository.countByMatchIdAndStatutPaiement(matchId, StatutPaiement.PAYE);
        if (nbPayes >= 4)
            throw new RuntimeException("Impossible : le match est complet");

        // Vérifier que le joueur n'est pas déjà inscrit ou annulé sur ce match
        boolean dejaInscrit = inscriptionMatchRepository
                .existsByMatchIdAndMembreId(matchId, membreId);
        if (dejaInscrit)
            throw new RuntimeException("Impossible : ce joueur est déjà inscrit à ce match");

        InscriptionMatch inscription = InscriptionMatch.builder()
                .match(match)
                .membre(membre)
                .statutPaiement(StatutPaiement.INSCRIT)
                .montantPaye(BigDecimal.ZERO)
                .build();

        return inscriptionMatchRepository.save(inscription);
    }

    // Payer sa place (match PRIVE)
    @Transactional
    public InscriptionMatch payerPlace(Long inscriptionId, Long membreId) {

        InscriptionMatch inscription = inscriptionMatchRepository.findById(inscriptionId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée : " + inscriptionId));

        // Vérifier que c'est bien le joueur lui-même qui paie
        if (!inscription.getMembre().getId().equals(membreId))
            throw new RuntimeException("Impossible : vous ne pouvez payer que votre propre place");

        // Vérifier que l'inscription est en statut INSCRIT
        if (inscription.getStatutPaiement() != StatutPaiement.INSCRIT)
            throw new RuntimeException("Impossible : cette place n'est pas en attente de paiement");

        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new RuntimeException("Membre non trouvé : " + membreId));

        // Montant = 15€ + soldeDu
        BigDecimal montant = BigDecimal.valueOf(15).add(membre.getSoldeDu());
        inscription.setStatutPaiement(StatutPaiement.PAYE);
        inscription.setMontantPaye(montant);
        inscription.setDatePaiement(LocalDateTime.now());

        // Remettre soldeDu à zéro
        membre.setSoldeDu(BigDecimal.ZERO);
        membreRepository.save(membre);

        InscriptionMatch saved = inscriptionMatchRepository.save(inscription);

        // Vérifier si le match est maintenant complet
        int nbPayesApres = inscriptionMatchRepository
                .countByMatchIdAndStatutPaiement(inscription.getMatch().getId(), StatutPaiement.PAYE);
        if (nbPayesApres >= 4) {
            Match match = inscription.getMatch();
            match.setStatut(StatutMatch.COMPLET);
            matchRepository.save(match);
        }

        return saved;
    }

    // S'inscrire ET payer en une fois (match PUBLIC ou DEVENU_PUBLIC)
    @Transactional
    public InscriptionMatch inscrireEtPayer(Long matchId, Long membreId) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match non trouvé : " + matchId));

        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new RuntimeException("Membre non trouvé : " + membreId));

        // Vérifier que c'est un match PUBLIC ou DEVENU_PUBLIC
        if (match.getType() == TypeMatch.PRIVE && match.getStatut() != StatutMatch.DEVENU_PUBLIC)
            throw new RuntimeException("Impossible : ce match n'est pas ouvert au public");

        // Vérifier que le match n'est pas complet (premier payé = premier servi)
        int nbPayes = inscriptionMatchRepository
                .countByMatchIdAndStatutPaiement(matchId, StatutPaiement.PAYE);
        if (nbPayes >= 4)
            throw new RuntimeException("Impossible : le match est complet, toutes les places sont prises");

        // Vérifier que le joueur n'est pas déjà inscrit
        boolean dejaInscrit = inscriptionMatchRepository
                .existsByMatchIdAndMembreId(matchId, membreId);
        if (dejaInscrit)
            throw new RuntimeException("Impossible : vous êtes déjà inscrit à ce match");

        // Montant = 15€ + soldeDu
        BigDecimal montant = BigDecimal.valueOf(15).add(membre.getSoldeDu());
        membre.setSoldeDu(BigDecimal.ZERO);
        membreRepository.save(membre);

        InscriptionMatch inscription = InscriptionMatch.builder()
                .match(match)
                .membre(membre)
                .statutPaiement(StatutPaiement.PAYE)
                .montantPaye(montant)
                .datePaiement(LocalDateTime.now())
                .build();

        InscriptionMatch saved = inscriptionMatchRepository.save(inscription);

        // Vérifier si le match est maintenant complet
        int nbPayesApres = inscriptionMatchRepository
                .countByMatchIdAndStatutPaiement(matchId, StatutPaiement.PAYE);
        if (nbPayesApres >= 4) {
            match.setStatut(StatutMatch.COMPLET);
            matchRepository.save(match);
        }

        return saved;
    }

    public InscriptionMatchResponse toResponse(InscriptionMatch i) {
        return InscriptionMatchResponse.builder()
                .id(i.getId())
                .matchId(i.getMatch() != null ? i.getMatch().getId() : null)
                .membreId(i.getMembre() != null ? i.getMembre().getId() : null)
                .nomMembre(i.getMembre() != null ? i.getMembre().getNom() : null)
                .prenomMembre(i.getMembre() != null ? i.getMembre().getPrenom() : null)
                .matriculeMembre(i.getMembre() != null ? i.getMembre().getMatricule() : null)
                .statutPaiement(i.getStatutPaiement())
                .montantPaye(i.getMontantPaye())
                .datePaiement(i.getDatePaiement())
                .build();
    }

    public List<InscriptionMatchResponse> getByMatchIdAsResponse(Long matchId) {
        return inscriptionMatchRepository.findByMatchId(matchId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<InscriptionMatchResponse> getByMembreIdAsResponse(Long membreId) {
        return inscriptionMatchRepository.findByMembreId(membreId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}