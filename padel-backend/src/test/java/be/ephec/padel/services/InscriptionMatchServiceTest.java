package be.ephec.padel.services;
import be.ephec.padel.models.InscriptionMatch;
import be.ephec.padel.models.Match;
import be.ephec.padel.models.Membre;
import be.ephec.padel.repositories.InscriptionMatchRepository;
import be.ephec.padel.repositories.MatchRepository;
import be.ephec.padel.repositories.MembreRepository;
import be.ephec.padel.models.enums.StatutMatch;
import be.ephec.padel.models.enums.StatutPaiement;
import be.ephec.padel.models.enums.TypeMatch;
import be.ephec.padel.models.enums.TypeMembre;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscriptionMatchServiceTest {

    @Mock
    private InscriptionMatchRepository inscriptionMatchRepository;
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private MembreRepository membreRepository;

    @InjectMocks
    private InscriptionMatchService inscriptionMatchService;

    private Membre buildMembre(Long id, BigDecimal soldeDu) {
        return Membre.builder()
                .id(id)
                .matricule("M" + id)
                .nom("Dupont")
                .prenom("Alice")
                .email("alice" + id + "@test.be")
                .type(TypeMembre.GLOBAL)
                .soldeDu(soldeDu)
                .build();
    }

    private Match buildMatch(Long id, TypeMatch type, StatutMatch statut, Membre organisateur) {
        return Match.builder()
                .id(id)
                .dateHeure(LocalDateTime.of(2026, 6, 10, 14, 0))
                .type(type)
                .statut(statut)
                .montantTotal(new BigDecimal("60"))
                .organisateur(organisateur)
                .build();
    }

    private InscriptionMatch buildInscription(Long id, Match match, Membre membre, StatutPaiement statut) {
        return InscriptionMatch.builder()
                .id(id)
                .match(match)
                .membre(membre)
                .statutPaiement(statut)
                .montantPaye(BigDecimal.ZERO)
                .build();
    }

    @Test
    void inscrireEtPayer_shouldThrow_whenMatchPriveNotDevenuPublic() {
        Membre organisateur = buildMembre(1L, BigDecimal.ZERO);
        Match match = buildMatch(1L, TypeMatch.PRIVE, StatutMatch.EN_ATTENTE, organisateur);
        Membre membre = buildMembre(2L, BigDecimal.ZERO);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(membreRepository.findById(2L)).thenReturn(Optional.of(membre));

        assertThatThrownBy(() -> inscriptionMatchService.inscrireEtPayer(1L, 2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("n'est pas ouvert au public");
    }

    @Test
    void inscrireEtPayer_shouldThrow_whenQuatrePlacesDejaPayees() {
        Membre organisateur = buildMembre(1L, BigDecimal.ZERO);
        Match match = buildMatch(1L, TypeMatch.PUBLIC, StatutMatch.EN_ATTENTE, organisateur);
        Membre membre = buildMembre(2L, BigDecimal.ZERO);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(membreRepository.findById(2L)).thenReturn(Optional.of(membre));
        when(inscriptionMatchRepository.countByMatchIdAndStatutPaiement(1L, StatutPaiement.PAYE)).thenReturn(4);

        assertThatThrownBy(() -> inscriptionMatchService.inscrireEtPayer(1L, 2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("le match est complet");
    }

    @Test
    void inscrireEtPayer_shouldChargeFifteenPlusSoldeDu_andResetSolde() {
        Membre organisateur = buildMembre(1L, BigDecimal.ZERO);
        Match match = buildMatch(1L, TypeMatch.PUBLIC, StatutMatch.EN_ATTENTE, organisateur);
        Membre membre = buildMembre(2L, new BigDecimal("5"));
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(membreRepository.findById(2L)).thenReturn(Optional.of(membre));
        when(inscriptionMatchRepository.countByMatchIdAndStatutPaiement(1L, StatutPaiement.PAYE)).thenReturn(1);
        when(inscriptionMatchRepository.existsByMatchIdAndMembreId(1L, 2L)).thenReturn(false);
        when(membreRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(inscriptionMatchRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        InscriptionMatch result = inscriptionMatchService.inscrireEtPayer(1L, 2L);

        assertThat(result.getMontantPaye()).isEqualByComparingTo(new BigDecimal("20"));
        assertThat(membre.getSoldeDu()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(membreRepository).save(membre);
    }

    @Test
    void inscrireEtPayer_shouldSetMatchComplet_whenFourthPaiementReached() {
        Membre organisateur = buildMembre(1L, BigDecimal.ZERO);
        Match match = buildMatch(1L, TypeMatch.PUBLIC, StatutMatch.EN_ATTENTE, organisateur);
        Membre membre = buildMembre(2L, BigDecimal.ZERO);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(membreRepository.findById(2L)).thenReturn(Optional.of(membre));
        when(inscriptionMatchRepository.countByMatchIdAndStatutPaiement(1L, StatutPaiement.PAYE)).thenReturn(3, 4);
        when(inscriptionMatchRepository.existsByMatchIdAndMembreId(1L, 2L)).thenReturn(false);
        when(membreRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(inscriptionMatchRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        inscriptionMatchService.inscrireEtPayer(1L, 2L);

        assertThat(match.getStatut()).isEqualTo(StatutMatch.COMPLET);
        verify(matchRepository).save(match);
    }

    @Test
    void payerPlace_shouldThrow_whenMembreTriesToPayAnotherPlace() {
        Membre organisateur = buildMembre(1L, BigDecimal.ZERO);
        Match match = buildMatch(1L, TypeMatch.PRIVE, StatutMatch.EN_ATTENTE, organisateur);
        Membre titulaire = buildMembre(2L, BigDecimal.ZERO);
        InscriptionMatch inscription = buildInscription(10L, match, titulaire, StatutPaiement.INSCRIT);
        when(inscriptionMatchRepository.findById(10L)).thenReturn(Optional.of(inscription));

        assertThatThrownBy(() -> inscriptionMatchService.payerPlace(10L, 3L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("vous ne pouvez payer que votre propre place");
    }

    @Test
    void payerPlace_shouldThrow_whenInscriptionNotInscrit() {
        Membre organisateur = buildMembre(1L, BigDecimal.ZERO);
        Match match = buildMatch(1L, TypeMatch.PRIVE, StatutMatch.EN_ATTENTE, organisateur);
        Membre membre = buildMembre(2L, BigDecimal.ZERO);
        InscriptionMatch inscription = buildInscription(10L, match, membre, StatutPaiement.PAYE);
        when(inscriptionMatchRepository.findById(10L)).thenReturn(Optional.of(inscription));

        assertThatThrownBy(() -> inscriptionMatchService.payerPlace(10L, 2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("n'est pas en attente de paiement");
    }

    @Test
    void inviter_shouldThrow_whenMatchNotPrive() {
        Membre organisateur = buildMembre(1L, BigDecimal.ZERO);
        Match match = buildMatch(1L, TypeMatch.PUBLIC, StatutMatch.EN_ATTENTE, organisateur);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        assertThatThrownBy(() -> inscriptionMatchService.inviter(1L, "M999", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Seul un match privé peut recevoir des invitations");
    }

    @Test
    void quitterMatch_shouldThrow_whenOrganisateurTriesToLeave() {
        Membre organisateur = buildMembre(1L, BigDecimal.ZERO);
        Match match = buildMatch(1L, TypeMatch.PRIVE, StatutMatch.EN_ATTENTE, organisateur);
        InscriptionMatch inscription = buildInscription(10L, match, organisateur, StatutPaiement.PAYE);
        when(inscriptionMatchRepository.findById(10L)).thenReturn(Optional.of(inscription));

        assertThatThrownBy(() -> inscriptionMatchService.quitterMatch(10L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("L'organisateur ne peut pas quitter le match");
    }

    @Test
    void quitterMatch_shouldSetAnnule_andReopenCompletMatch() {
        Membre organisateur = buildMembre(1L, BigDecimal.ZERO);
        Match match = buildMatch(1L, TypeMatch.PRIVE, StatutMatch.COMPLET, organisateur);
        Membre joueur = buildMembre(2L, BigDecimal.ZERO);
        InscriptionMatch inscription = buildInscription(10L, match, joueur, StatutPaiement.PAYE);
        when(inscriptionMatchRepository.findById(10L)).thenReturn(Optional.of(inscription));
        when(inscriptionMatchRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        inscriptionMatchService.quitterMatch(10L, 2L);

        assertThat(inscription.getStatutPaiement()).isEqualTo(StatutPaiement.ANNULE);
        assertThat(match.getStatut()).isEqualTo(StatutMatch.EN_ATTENTE);
        verify(matchRepository).save(match);
    }
}