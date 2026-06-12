package be.ephec.padel.match;

import be.ephec.padel.fermeture.JourFermetureRepository;
import be.ephec.padel.membre.Membre;
import be.ephec.padel.membre.MembreRepository;
import be.ephec.padel.membre.TypeMembre;
import be.ephec.padel.site.Site;
import be.ephec.padel.terrain.Terrain;
import be.ephec.padel.terrain.TerrainRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock private MatchRepository matchRepository;
    @Mock private MembreRepository membreRepository;
    @Mock private TerrainRepository terrainRepository;
    @Mock private JourFermetureRepository jourFermetureRepository;

    @InjectMocks
    private MatchService matchService;

    private Match buildMatch(Long id, TypeMatch type, StatutMatch statut) {
        return Match.builder()
                .id(id)
                .dateHeure(LocalDateTime.of(2025, 6, 10, 14, 0))
                .type(type)
                .statut(statut)
                .montantTotal(new BigDecimal("60"))
                .build();
    }

    @Test
    void getAll_shouldReturnAllMatchs() {
        Match m1 = buildMatch(1L, TypeMatch.PRIVE, StatutMatch.EN_ATTENTE);
        Match m2 = buildMatch(2L, TypeMatch.PUBLIC, StatutMatch.COMPLET);
        when(matchRepository.findAll()).thenReturn(List.of(m1, m2));
        List<Match> result = matchService.getAll();
        assertThat(result).hasSize(2).containsExactly(m1, m2);
    }

    @Test
    void getPublics_shouldReturnOnlyPublicMatchs() {
        Match m1 = buildMatch(1L, TypeMatch.PUBLIC, StatutMatch.EN_ATTENTE);
        Match m2 = buildMatch(2L, TypeMatch.PUBLIC, StatutMatch.COMPLET);
        when(matchRepository.findByType(TypeMatch.PUBLIC)).thenReturn(List.of(m1, m2));
        List<Match> result = matchService.getPublics();
        assertThat(result).hasSize(2).allMatch(m -> m.getType() == TypeMatch.PUBLIC);
    }

    @Test
    void getByStatut_shouldReturnMatchsWithStatut() {
        Match m1 = buildMatch(1L, TypeMatch.PRIVE, StatutMatch.ANNULE);
        when(matchRepository.findByStatut(StatutMatch.ANNULE)).thenReturn(List.of(m1));
        List<Match> result = matchService.getByStatut(StatutMatch.ANNULE);
        assertThat(result).hasSize(1).allMatch(m -> m.getStatut() == StatutMatch.ANNULE);
    }

    @Test
    void create_shouldSaveAndReturnMatch() {
        Site site = Site.builder().id(1L).nom("Padel Ixelles").build();
        Terrain terrain = Terrain.builder().id(1L).site(site).build();
        Membre organisateur = Membre.builder()
                .id(1L).matricule("G0001").nom("Dubois").prenom("Marie")
                .type(TypeMembre.GLOBAL).soldeDu(BigDecimal.ZERO).build();

        Match match = Match.builder()
                .dateHeure(LocalDateTime.now().plusDays(30))
                .type(TypeMatch.PRIVE).statut(StatutMatch.EN_ATTENTE)
                .montantTotal(new BigDecimal("60"))
                .organisateur(organisateur).terrain(terrain).build();

        Match saved = Match.builder().id(5L)
                .dateHeure(match.getDateHeure())
                .type(TypeMatch.PRIVE).statut(StatutMatch.EN_ATTENTE)
                .montantTotal(new BigDecimal("60"))
                .organisateur(organisateur).terrain(terrain).build();

        when(membreRepository.findById(1L)).thenReturn(Optional.of(organisateur));
        when(terrainRepository.findById(1L)).thenReturn(Optional.of(terrain));
        when(jourFermetureRepository.existsBySiteIsNullAndDate(any())).thenReturn(false);
        when(jourFermetureRepository.existsBySiteIdAndDate(any(), any())).thenReturn(false);
        when(matchRepository.save(any())).thenReturn(saved);

        Match result = matchService.create(match);

        assertThat(result.getId()).isEqualTo(5L);
        verify(matchRepository).save(any());
    }
}